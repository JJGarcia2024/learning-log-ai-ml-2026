"""
Munger Protocol — CLI Index Scanner
====================================
Batch stock analysis using the Charlie Munger investment framework.

Data Sources:
  - Yahoo Finance (yfinance) for all financial data
  - Antigravity CLI web search for qualitative research sections

Usage:
  # 1. Fetch financial data for an index
  python munger_cli_scanner.py fetch --index DJIA --max 5

  # 2. (Antigravity fills research JSON files via web search)

  # 3. Generate markdown reports
  python munger_cli_scanner.py report --ticker AAPL
  python munger_cli_scanner.py report --all

  # List tickers in an index
  python munger_cli_scanner.py list --index SP500

No API keys required. No external AI costs.
"""

# ─── Standard library ─────────────────────────────────────────────────────────
from __future__ import print_function
import argparse
import json
import math
import os
import sys
import time
import traceback
from datetime import date
from pathlib import Path

# ─── Third-party (minimal) ────────────────────────────────────────────────────
import pandas as pd
import yfinance as yf

# tabulate is optional for the fetch path but used in report rendering
try:
    from tabulate import tabulate
except ImportError:
    tabulate = None

# ===========================================================================
# CONFIGURATION
# ===========================================================================

DEFAULT_OUTPUT_DIR = "munger_output"
DATA_SUBDIR = "data"
RESEARCH_SUBDIR = "research"
REPORTS_SUBDIR = "reports"

DJIA_FALLBACK = sorted(
    [
        "AAPL",
        "AMGN",
        "AXP",
        "BA",
        "CAT",
        "CRM",
        "CSCO",
        "CVX",
        "DIS",
        "DOW",
        "GS",
        "HD",
        "HON",
        "IBM",
        "INTC",
        "JNJ",
        "JPM",
        "KO",
        "MCD",
        "MMM",
        "MRK",
        "MSFT",
        "NKE",
        "PG",
        "TRV",
        "UNH",
        "V",
        "VZ",
        "WBA",
        "WMT",
    ]
)

# ─── Financial statement key maps ─────────────────────────────────────────────
INCOME_MAP = {
    "revenue": ["Total Revenue"],
    "gross_profit": ["Gross Profit"],
    "operating_income": ["Operating Income", "EBIT"],
    "net_income": ["Net Income"],
    "dna": [
        "Reconciled Depreciation",
        "Depreciation And Amortization In Income Statement",
    ],
}

BALANCE_MAP = {
    "total_assets": ["Total Assets"],
    "total_liabilities": ["Total Liabilities Net Minority Interest"],
    "total_equity": ["Stockholders Equity", "Total Equity Gross Minority Interest"],
    "cash": [
        "Cash And Cash Equivalents",
        "Cash Cash Equivalents And Short Term Investments",
    ],
    "total_debt": ["Total Debt"],
    "current_assets": ["Current Assets"],
    "current_liabilities": ["Current Liabilities"],
    "shares_outstanding": ["Ordinary Shares Number", "Share Issued"],
}

CASHFLOW_MAP = {
    "operating_cf": ["Operating Cash Flow"],
    "capex": ["Capital Expenditure"],
    "free_cash_flow": ["Free Cash Flow"],
    "dividends_paid": ["Cash Dividends Paid"],
    "buybacks": ["Repurchase Of Capital Stock"],
}

# ─── Search query templates for Antigravity orchestrator ───────────────────────
SEARCH_QUERIES = {
    "section_1": [
        "{company} {ticker} revenue predictability recurring demand analysis 2025 2026",
        "{company} {ticker} business model complexity investor circle of competence",
        "{company} {ticker} 10 year earnings outlook business stability forecast",
    ],
    "section_2": [
        "{company} {ticker} competitive moat analysis switching costs brand advantage",
        "{company} {ticker} network effects scale advantages cost leadership",
        "{company} {ticker} patents regulatory barriers intellectual property moat",
        "{company} {ticker} competitive threats risks disruption challenges 2025 2026",
        "{company} {ticker} moat widening narrowing competitive position trend",
    ],
    "section_3": [
        "{company} {ticker} CEO management candor shareholder letter transparency",
        "{company} {ticker} executive compensation insider ownership stock options proxy",
        "{company} {ticker} key man risk CEO succession plan leadership",
    ],
    "section_5": [
        "{company} {ticker} stock valuation fair value analyst price target 2025 2026",
    ],
    "section_6": [
        "{company} {ticker} investor sentiment hype contrarian analysis bias",
    ],
    "section_7": [
        "{company} {ticker} market position monopoly commodity pricing power ecosystem",
        "{company} {ticker} growth engines expansion opportunities critical mass",
    ],
    "section_8": [
        "{company} {ticker} investment thesis bull bear case converging forces",
    ],
    "section_9": [
        "{company} {ticker} investment risk permanent capital loss downside analysis",
        "{company} {ticker} opportunity cost alternative investments comparison",
    ],
}

# Sectors that require analyst review for circle-of-competence
COMPLEX_SECTORS = ["Healthcare", "Financials", "Technology", "Energy", "Utilities"]


# ===========================================================================
# UTILITY FUNCTIONS
# ===========================================================================


def safe_float(val, default=0.0):
    """Safely convert a value to float, stripping commas and % signs."""
    try:
        return float(str(val).replace(",", "").replace("%", ""))
    except (ValueError, TypeError):
        return default


def fmt_m(val):
    """Format a number as a human-readable dollar amount ($1.23B, $456.7M)."""
    try:
        v = float(val)
        if abs(v) >= 1e9:
            return "${:,.2f}B".format(v / 1e9)
        if abs(v) >= 1e6:
            return "${:,.1f}M".format(v / 1e6)
        return "${:,.0f}".format(v)
    except (ValueError, TypeError):
        return "N/A"


def fmt_pct(val, decimals=1):
    """Format a number as a percentage string."""
    try:
        return "{:.{d}f}%".format(float(val), d=decimals)
    except (ValueError, TypeError):
        return "N/A"


def fmt_ratio(val, decimals=2):
    """Format a number as a ratio string (e.g. 1.23x)."""
    try:
        return "{:.{d}f}x".format(float(val), d=decimals)
    except (ValueError, TypeError):
        return "N/A"


def fmt_price(val):
    """Format a number as a dollar price."""
    try:
        return "${:,.2f}".format(float(val))
    except (ValueError, TypeError):
        return "N/A"


def sanitize_for_json(obj):
    """Recursively replace NaN / Inf with None so json.dumps succeeds."""
    if isinstance(obj, float):
        if math.isnan(obj) or math.isinf(obj):
            return None
        return obj
    elif isinstance(obj, dict):
        return {k: sanitize_for_json(v) for k, v in obj.items()}
    elif isinstance(obj, list):
        return [sanitize_for_json(v) for v in obj]
    return obj


def check_mark(val):
    """Return a checkbox mark for booleans."""
    if val:
        return "x"
    return " "


# ===========================================================================
# YFINANCE DATA HELPERS
# ===========================================================================


def yf_to_rows(df):
    """Convert a yfinance DataFrame (metrics x dates) into a list of dicts."""
    if df is None or df.empty:
        return []
    rows = []
    for col in df.columns:
        row = {"calendarYear": str(col.year)}
        for metric in df.index:
            val = df.loc[metric, col]
            row[metric] = None if pd.isna(val) else float(val)
        rows.append(row)
    return rows


def parse_stmts(rows, key_map):
    """Parse yfinance rows into a clean DataFrame using a key mapping."""
    records = []
    for row in rows:
        rec = {"fiscal_year": row.get("calendarYear", "N/A")}
        for col, keys in key_map.items():
            keys = keys if isinstance(keys, list) else [keys]
            for k in keys:
                if row.get(k) is not None:
                    rec[col] = safe_float(row[k])
                    break
            if col not in rec:
                rec[col] = 0.0
        records.append(rec)
    df = pd.DataFrame(records)
    if not df.empty:
        df = df.sort_values("fiscal_year").reset_index(drop=True)
    return df


# ===========================================================================
# INDEX TICKER FETCHING
# ===========================================================================


def get_index_tickers(idx):
    """Fetch ticker symbols for a stock index from Wikipedia."""
    if idx == "DJIA":
        try:
            url = "https://en.wikipedia.org/wiki/Dow_Jones_Industrial_Average"
            tables = pd.read_html(url)
            for t in tables:
                for col in ["Symbol", "Ticker"]:
                    if col in t.columns:
                        tks = t[col].dropna().astype(str).tolist()
                        tks = [
                            x.strip().replace(".", "-")
                            for x in tks
                            if 1 < len(x.strip()) <= 5
                        ]
                        if len(tks) >= 25:
                            return sorted(tks)
        except Exception as e:
            print("  [WARN] Wikipedia fetch failed: {}. Using fallback list.".format(e))
        return list(DJIA_FALLBACK)

    elif idx == "SP500":
        url = "https://en.wikipedia.org/wiki/List_of_S%26P_500_companies"
        import requests

        html = requests.get(url, headers={"User-Agent": "Mozilla/5.0"}).text
        df = pd.read_html(html)[0]
        tks = (
            df["Symbol"]
            .astype(str)
            .str.strip()
            .str.replace(".", "-", regex=False)
            .tolist()
        )
        return sorted(tks)

    elif idx == "NASDAQ100":
        url = "https://en.wikipedia.org/wiki/Nasdaq-100"
        import requests
        html = requests.get(url, headers={'User-Agent': 'Mozilla/5.0'}).text
        tables = pd.read_html(html)
        for t in tables:
            for col in ["Ticker", "Symbol"]:
                if col in t.columns:
                    tks = t[col].dropna().astype(str).tolist()
                    tks = [x.strip() for x in tks if 1 < len(x.strip()) <= 5]
                    if len(tks) >= 90:
                        tks.append("SPCX")
                        return sorted(list(set(tks)))
        raise ValueError("Could not parse NASDAQ-100 table from Wikipedia.")

    elif idx == "RUSSELL3000E":
        import os
        csv_path = "russell3000.csv"
        if not os.path.exists(csv_path):
            raise ValueError(f"Could not find {csv_path}. Please ensure it exists.")
        with open(csv_path, "r") as f:
            tks = [line.strip() for line in f if line.strip()]
        return sorted(list(set(tks)))

    else:
        raise ValueError(
            'Unknown index "{}". Supported: DJIA, SP500, NASDAQ100, RUSSELL3000E'.format(idx)
        )


# ===========================================================================
# FETCH & COMPUTE — Core financial data pipeline
# ===========================================================================


def build_search_queries(company_name, ticker):
    """Generate search query strings for the Antigravity orchestrator."""
    queries = {}
    for section, templates in SEARCH_QUERIES.items():
        queries[section] = [
            tpl.format(company=company_name, ticker=ticker) for tpl in templates
        ]
    return queries


def fetch_and_compute(ticker, risk_free_rate=4.35, maint_capex_ratio=0.70):
    """
    Fetch financial data from yfinance for a single ticker,
    compute all Munger Protocol metrics, and return a dict
    ready for JSON serialization.
    """
    yf_t = yf.Ticker(ticker)
    info = yf_t.info or {}

    # ── Company identity ───────────────────────────────────────────────────
    d = {
        "ticker": ticker,
        "company_name": info.get("longName", ticker),
        "sector": info.get("sector", "N/A"),
        "industry": info.get("industry", "N/A"),
        "description": (
            info.get("longBusinessSummary", "No description available.")
            or "No description available."
        )[:600],
        "employees": str(info.get("fullTimeEmployees", "N/A")),
        "exchange": info.get("exchange", "N/A"),
        "website": info.get("website", "N/A"),
    }

    # ── Market data ────────────────────────────────────────────────────────
    d["current_price"] = safe_float(
        info.get("currentPrice", info.get("regularMarketPrice", 0))
    )
    d["market_cap"] = safe_float(info.get("marketCap", 0))
    d["shares_out"] = safe_float(info.get("sharesOutstanding", 0))
    if d["market_cap"] == 0 and d["current_price"] > 0 and d["shares_out"] > 0:
        d["market_cap"] = d["current_price"] * d["shares_out"]
    d["pe_ratio"] = safe_float(info.get("trailingPE", 0))

    # ── Parse financial statements ─────────────────────────────────────────
    df_i = parse_stmts(yf_to_rows(yf_t.financials), INCOME_MAP)
    df_b = parse_stmts(yf_to_rows(yf_t.balance_sheet), BALANCE_MAP)
    df_c = parse_stmts(yf_to_rows(yf_t.cashflow), CASHFLOW_MAP)

    # Merge into a single wide DataFrame
    df = df_i.merge(df_c, on="fiscal_year", how="outer", suffixes=("_inc", "_cf"))
    df = df.merge(df_b, on="fiscal_year", how="outer")
    df = df.sort_values("fiscal_year").reset_index(drop=True)

    # ── Derived metrics ────────────────────────────────────────────────────
    df["net_wc"] = df["current_assets"] - df["current_liabilities"]
    df["wc_change"] = df["net_wc"].diff().fillna(0) * -1
    df["capex_abs"] = df["capex"].abs()
    df["maint_capex"] = df["capex_abs"] * maint_capex_ratio
    df["owner_earnings"] = (
        df["net_income"] + df["dna"] + df["wc_change"] - df["maint_capex"]
    )
    df["gross_margin_pct"] = (
        df["gross_profit"] / df["revenue"].replace(0, float("nan")) * 100
    )
    df["rev_growth_pct"] = df["revenue"].pct_change() * 100

    # ── Serialize financial data (NaN → None) ─────────────────────────────
    d["financial_data"] = sanitize_for_json(df.to_dict(orient="records"))

    # ── Latest-period snapshot ─────────────────────────────────────────────
    lat = df.iloc[-1] if not df.empty else pd.Series(dtype=float)

    d["latest_oe"] = safe_float(lat.get("owner_earnings", 0))
    d["latest_cash"] = safe_float(lat.get("cash", 0))
    d["latest_debt"] = safe_float(lat.get("total_debt", 0))
    d["ev"] = d["market_cap"] + d["latest_debt"] - d["latest_cash"]
    d["oe_yield"] = (d["latest_oe"] / d["ev"] * 100) if d["ev"] > 0 else 0

    lat_ni = safe_float(lat.get("net_income", 0))
    lat_eq = safe_float(lat.get("total_equity", 1)) or 1
    if d["pe_ratio"] == 0 and lat_ni > 0:
        d["pe_ratio"] = d["market_cap"] / lat_ni
    d["roe"] = lat_ni / lat_eq * 100
    d["debt_equity"] = d["latest_debt"] / lat_eq
    d["latest_gm"] = safe_float(lat.get("gross_margin_pct", 0))
    d["current_ratio"] = safe_float(lat.get("current_assets", 0)) / max(
        safe_float(lat.get("current_liabilities", 1)), 1
    )

    # ── Latest-period raw values for report Section 4 ──────────────────────
    d["latest_net_income"] = lat_ni
    d["latest_dna"] = safe_float(lat.get("dna", 0))
    d["latest_wc_change"] = safe_float(lat.get("wc_change", 0))
    d["latest_maint_capex"] = safe_float(lat.get("maint_capex", 0))
    d["latest_net_wc"] = safe_float(lat.get("net_wc", 0))
    d["latest_current_assets"] = safe_float(lat.get("current_assets", 0))
    d["latest_current_liabilities"] = safe_float(lat.get("current_liabilities", 0))

    # ── Margin of safety flag ──────────────────────────────────────────────
    if d["oe_yield"] > risk_free_rate * 1.5:
        d["mos_flag"] = "YES — yield is attractive vs. risk-free rate"
    elif d["oe_yield"] > risk_free_rate:
        d["mos_flag"] = "BORDERLINE — yield is close to risk-free rate"
    else:
        d["mos_flag"] = "NO — yield does not compensate for business risk"

    # ── Lollapalooza signals (auto-detected from data) ─────────────────────
    sigs = []
    if d["latest_gm"] > 40:
        sigs.append("Pricing power (GM {:.1f}%)".format(d["latest_gm"]))

    oe_pct_changes = df["owner_earnings"].pct_change().dropna()
    oe_avg_growth = oe_pct_changes.mean() * 100 if len(oe_pct_changes) > 0 else 0
    if not math.isnan(oe_avg_growth) and oe_avg_growth > 10:
        sigs.append("OE growing avg {:.1f}% p.a.".format(oe_avg_growth))

    if d["roe"] > 15:
        sigs.append("High ROE {:.1f}%".format(d["roe"]))
    if d["debt_equity"] < 0.5:
        sigs.append("Low leverage ({:.2f}x D/E)".format(d["debt_equity"]))
    d["lolla_signals"] = sigs

    # ── Section 1A auto-fill (circle of competence) ────────────────────────
    if d["sector"] in COMPLEX_SECTORS:
        d["s1a"] = "REVIEW (analyst confirmed) — complex sector: " + d["sector"]
    else:
        d["s1a"] = "YES — business model is understandable (" + d["sector"] + ")"

    # ── Search queries for Antigravity orchestrator ────────────────────────
    d["search_queries"] = build_search_queries(d["company_name"], ticker)

    # ── Config / metadata ──────────────────────────────────────────────────
    d["risk_free_rate"] = risk_free_rate
    d["maint_capex_ratio"] = maint_capex_ratio
    d["fetch_date"] = str(date.today())

    return sanitize_for_json(d)


# ===========================================================================
# REPORT GENERATION — Markdown renderer
# ===========================================================================


def _safe_get(d, *keys, **kwargs):
    """Drill into nested dicts safely. Returns default on any miss."""
    default = kwargs.get("default", "N/A")
    current = d
    for k in keys:
        if isinstance(current, dict):
            current = current.get(k, None)
        else:
            return default
        if current is None:
            return default
    return current


def _moat_row(research, moat_key, label):
    """Build a moat table row from research JSON."""
    applies = _safe_get(research, "section_2", moat_key, "applies", default=False)
    evidence = _safe_get(research, "section_2", moat_key, "evidence", default="—")
    mark = "✅" if applies else "❌"
    return "| {} | {} | {} |".format(label, mark, evidence)


def _failure_row(research, mode_key, label):
    """Build a failure-mode table row from research JSON."""
    pct = _safe_get(
        research, "section_2", "failure_modes", mode_key, "pct", default="—"
    )
    rationale = _safe_get(
        research, "section_2", "failure_modes", mode_key, "rationale", default="—"
    )
    return "| {} | {}% | {} |".format(label, pct, rationale)


def _bias_row(research, bias_key, label):
    """Build a psychological bias table row from research JSON."""
    risk = _safe_get(research, "section_6", bias_key, "risk", default="—")
    note = _safe_get(research, "section_6", bias_key, "note", default="—")
    return "| {} | {} | {} |".format(label, risk, note)


def _checklist_row(research, item_key, label):
    """Build a checklist table row from research JSON."""
    verdict = _safe_get(
        research, "section_9", "checklist", item_key, "verdict", default="—"
    )
    note = _safe_get(research, "section_9", "checklist", item_key, "note", default="—")
    return "| {} | {} | {} |".format(label, verdict, note)


def generate_report(data, research):
    """
    Generate a complete Munger Protocol Markdown report from data + research JSON.
    Returns the report as a string.
    """
    lines = []

    def add(line=""):
        lines.append(line)

    ticker = data.get("ticker", "???")
    company = data.get("company_name", ticker)
    report_date = data.get("fetch_date", str(date.today()))

    # ── Header ─────────────────────────────────────────────────────────────
    add("# 📊 Munger Protocol — Stock Valuation Report")
    add()
    add("| Field | Value |")
    add("|-------|-------|")
    add("| **Company** | {} ({}) |".format(company, ticker))
    add(
        "| **Exchange** | {} \\| Sector: {} \\| Industry: {} |".format(
            data.get("exchange", "N/A"),
            data.get("sector", "N/A"),
            data.get("industry", "N/A"),
        )
    )
    add("| **Date** | {} |".format(report_date))
    add("| **Analyst** | Antigravity CLI Scanner |")
    add(
        "| **Price** | {} \\| Market Cap: {} |".format(
            fmt_price(data.get("current_price", 0)),
            fmt_m(data.get("market_cap", 0)),
        )
    )
    add(
        "| **Employees** | {} \\| Website: {} |".format(
            data.get("employees", "N/A"),
            data.get("website", "N/A"),
        )
    )
    add()
    add("> {}".format(data.get("description", "No description available.")))
    add()
    add("---")
    add()

    # ── Section 1: Circle of Competence ────────────────────────────────────
    add("## Section 1: Circle of Competence Filter")
    add()
    add("| Test | Result |")
    add("|------|--------|")
    add("| **1A. Business Simplicity** | {} |".format(data.get("s1a", "N/A")))
    add(
        "| **1B. 10-Year Predictability** | {} |".format(
            _safe_get(research, "section_1", "1b_predictability")
        )
    )
    add(
        "| **1C. Statement of Competence** | {} |".format(
            _safe_get(research, "section_1", "1c_competence")
        )
    )
    add(
        "| **1D. Confidence Interval** | {} |".format(
            _safe_get(research, "section_1", "1d_confidence")
        )
    )
    add()
    add("---")
    add()

    # ── Section 2: Moat Analysis ───────────────────────────────────────────
    add("## Section 2: Moat Analysis")
    add()
    add("### 2A. Nature of the Moat")
    add()
    add("| Moat Type | Applies | Evidence |")
    add("|-----------|---------|----------|")
    add(_moat_row(research, "moat_supply_side", "Supply-Side (Cost/Scale)"))
    add(_moat_row(research, "moat_demand_side", "Demand-Side (Brand/Switching)"))
    add(_moat_row(research, "moat_network_effect", "Network Effect"))
    add(_moat_row(research, "moat_ip_regulatory", "IP / Regulatory"))
    add()

    # 2B: Gross Margin Trend
    add("### 2B. Gross Margin Trend (Pricing Power)")
    add()
    fin_data = data.get("financial_data", [])
    add("| Year | Revenue | Gross Profit | GM % |")
    add("|------|---------|-------------|------|")
    for row in fin_data:
        yr = row.get("fiscal_year", "—")
        rev = fmt_m(row.get("revenue", 0))
        gp = fmt_m(row.get("gross_profit", 0))
        gm = fmt_pct(row.get("gross_margin_pct", 0))
        add("| {} | {} | {} | {} |".format(yr, rev, gp, gm))
    add()

    # 2C: Revenue Growth
    add("### 2C. Revenue Growth")
    add()
    add("| Year | Revenue | YoY % |")
    add("|------|---------|-------|")
    for row in fin_data:
        yr = row.get("fiscal_year", "—")
        rev = fmt_m(row.get("revenue", 0))
        yoy = fmt_pct(row.get("rev_growth_pct", None))
        add("| {} | {} | {} |".format(yr, rev, yoy))
    add()

    # 2D: Lollapalooza Potential
    add("### 2D. Lollapalooza Potential")
    add()
    add(
        _safe_get(
            research,
            "section_2",
            "lollapalooza_potential",
            default="*No research data available.*",
        )
    )
    add()

    # 2E: Failure Modes
    add("### 2E. Inversion Pre-Mortem — Failure Mode Probabilities")
    add()
    add("| Failure Mode | Probability | Rationale |")
    add("|-------------|------------|----------|")
    add(_failure_row(research, "tech_disruption", "Tech Disruption"))
    add(_failure_row(research, "regulatory", "Regulatory"))
    add(_failure_row(research, "brand_erosion", "Brand Erosion"))
    add(_failure_row(research, "competitor", "Competitor"))
    add(_failure_row(research, "misallocation", "Capital Misallocation"))
    add()

    # 2F: Moat Trajectory
    add("### 2F. Moat Trajectory")
    add()
    trajectory = _safe_get(research, "section_2", "moat_trajectory", default="UNKNOWN")
    trajectory_ev = _safe_get(
        research, "section_2", "moat_trajectory_evidence", default="—"
    )
    add("**{}** — {}".format(trajectory, trajectory_ev))
    add()
    add("---")
    add()

    # ── Section 3: Management ──────────────────────────────────────────────
    add("## Section 3: Management Integrity & Talent Audit")
    add()

    # 3A: Candor
    add("### 3A. The Paper Test (Candor Assessment)")
    add()
    add(
        _safe_get(
            research,
            "section_3",
            "candor_assessment",
            default="*No research data available.*",
        )
    )
    add()

    # 3B: Capital Allocation
    add("### 3B. Capital Allocation Track Record")
    add()
    add("| Year | CapEx | Buybacks | Dividends |")
    add("|------|-------|----------|-----------|")
    for row in fin_data:
        yr = row.get("fiscal_year", "—")
        capex = fmt_m(abs(row.get("capex", 0) or 0))
        buybacks = fmt_m(abs(row.get("buybacks", 0) or 0))
        divs = fmt_m(abs(row.get("dividends_paid", 0) or 0))
        add("| {} | {} | {} | {} |".format(yr, capex, buybacks, divs))
    add()

    # 3C: Incentive Structure
    add("### 3C. Incentive Structure")
    add()
    s3 = research.get("section_3", {}) if isinstance(research, dict) else {}
    add("- [{}] Paid on EPS".format(check_mark(s3.get("paid_on_eps", False))))
    add("- [{}] Paid on ROIC".format(check_mark(s3.get("paid_on_roic", False))))
    add("- [{}] Options expensed".format(check_mark(s3.get("options_expensed", False))))
    add(
        "- [{}] High insider ownership ({})".format(
            check_mark(s3.get("insider_ownership", False)),
            s3.get("insider_pct", "N/A"),
        )
    )
    add()

    # 3D: Key-Man Risk
    add("### 3D. Institution vs. Individual (Key-Man Risk)")
    add()
    add(
        _safe_get(
            research, "section_3", "keymanrisk", default="*No research data available.*"
        )
    )
    add()
    add("---")
    add()

    # ── Section 4: Financial Forensics ─────────────────────────────────────
    add("## Section 4: Financial Forensics (Owner Earnings)")
    add()

    # 4A: OE Reconciliation
    add("### 4A. Owner Earnings Reconciliation (Most Recent Year)")
    add()
    add("| Item | Amount | Note |")
    add("|------|--------|------|")
    add(
        "| Net Income (GAAP) | {} | Starting point |".format(
            fmt_m(data.get("latest_net_income", 0))
        )
    )
    add(
        "| (+) Depreciation & Amort. | {} | Non-cash add-back |".format(
            fmt_m(data.get("latest_dna", 0))
        )
    )
    add(
        "| (+/-) Working Capital Chg | {} | Liquidity impact |".format(
            fmt_m(data.get("latest_wc_change", 0))
        )
    )
    add(
        "| (-) Maintenance CapEx | {} | ~{}% of CapEx |".format(
            fmt_m(data.get("latest_maint_capex", 0)),
            int(data.get("maint_capex_ratio", 0.70) * 100),
        )
    )
    add(
        "| **== OWNER EARNINGS ==** | **{}** | **True shareholder cash** |".format(
            fmt_m(data.get("latest_oe", 0))
        )
    )
    add()

    # 4B: Balance Sheet
    add("### 4B. Balance Sheet (Nuclear Winter Test)")
    add()
    net_cash = (data.get("latest_cash") or 0) - (data.get("latest_debt") or 0)
    stress_oe = (data.get("latest_oe") or 0) * 0.5
    add("| Metric | Value |")
    add("|--------|-------|")
    add("| Cash | {} |".format(fmt_m(data.get("latest_cash", 0))))
    add("| Total Debt | {} |".format(fmt_m(data.get("latest_debt", 0))))
    add("| Net Cash | {} |".format(fmt_m(net_cash)))
    add("| D/E Ratio | {} |".format(fmt_ratio(data.get("debt_equity", 0))))
    add("| Current Ratio | {} |".format(fmt_ratio(data.get("current_ratio", 0))))
    add("| 50% Stress OE | {} |".format(fmt_m(stress_oe)))
    add()
    add("---")
    add()

    # ── Section 5: Valuation ───────────────────────────────────────────────
    add("## Section 5: Valuation & Margin of Safety")
    add()

    # 5A: Core Summary
    add("### 5A. Core Valuation Summary")
    add()
    add("| Metric | Value |")
    add("|--------|-------|")
    add("| Market Cap | {} |".format(fmt_m(data.get("market_cap", 0))))
    add("| Enterprise Value | {} |".format(fmt_m(data.get("ev", 0))))
    add("| Owner Earnings | {} |".format(fmt_m(data.get("latest_oe", 0))))
    add("| OE Yield | {} |".format(fmt_pct(data.get("oe_yield", 0))))
    add(
        "| Risk-Free Rate (10y) | {} |".format(
            fmt_pct(data.get("risk_free_rate", 4.35))
        )
    )
    add("| P/E Ratio | {} |".format(fmt_ratio(data.get("pe_ratio", 0))))
    add("| Return on Equity | {} |".format(fmt_pct(data.get("roe", 0))))
    add("| Debt / Equity | {} |".format(fmt_ratio(data.get("debt_equity", 0))))
    add("| Margin of Safety | {} |".format(data.get("mos_flag", "N/A")))
    add()

    # 5B: 10-Year Projection
    add("### 5B. 10-Year Projection (OE / Risk-Free Rate)")
    add()
    latest_oe = data.get("latest_oe", 0) or 0
    rfr = data.get("risk_free_rate", 4.35) or 4.35
    rfr_decimal = rfr / 100.0
    add("| Growth | OE in 10Y | Implied Value |")
    add("|--------|-----------|---------------|")
    for rate in [3, 5, 8, 12]:
        try:
            oe_10y = latest_oe * ((1 + rate / 100.0) ** 10)
            implied_val = oe_10y / rfr_decimal if rfr_decimal > 0 else 0
            add("| {}% | {} | {} |".format(rate, fmt_m(oe_10y), fmt_m(implied_val)))
        except (ValueError, TypeError, ZeroDivisionError):
            add("| {}% | N/A | N/A |".format(rate))
    add()

    # 5D: Analyst Assessment
    add("### 5D. Analyst Assessment")
    add()
    add(
        _safe_get(
            research,
            "section_5",
            "price_assessment",
            default="*No research data available.*",
        )
    )
    add()
    add("---")
    add()

    # ── Section 6: Psychological Checklist ─────────────────────────────────
    add("## Section 6: Psychological Checklist (Munger Bias Audit)")
    add()
    add("| Bias | Risk | Note |")
    add("|------|------|------|")
    add(_bias_row(research, "incentive_bias", "Incentive Bias"))
    add(_bias_row(research, "confirmation_bias", "Confirmation Bias"))
    add(_bias_row(research, "social_proof", "Social Proof"))
    add(_bias_row(research, "liking_bias", "Liking Bias"))
    add(_bias_row(research, "doubt_avoidance", "Doubt Avoidance"))
    add(_bias_row(research, "inconsistency", "Inconsistency"))
    add(_bias_row(research, "authority_bias", "Authority Bias"))
    add(_bias_row(research, "deprival", "Deprival Super-Reaction"))
    add(_bias_row(research, "patience", "Patience"))
    add(_bias_row(research, "lollapalooza_risk", "Lollapalooza Risk"))
    add()
    add(
        "> **Serpico Effect:** {}".format(
            _safe_get(research, "section_6", "serpico_effect", default="—")
        )
    )
    add()
    add("---")
    add()

    # ── Section 7: Multidisciplinary Ecosystem ─────────────────────────────
    add("## Section 7: Multidisciplinary Ecosystem Analysis")
    add()

    # 7A
    add("### 7A. Physics — Critical Mass & Breakpoints")
    add()
    add(
        _safe_get(
            research,
            "section_7",
            "critical_mass",
            default="*No research data available.*",
        )
    )
    add()
    add(
        "**Operational Margin of Safety:** {}".format(
            _safe_get(research, "section_7", "op_margin_safety", default="—")
        )
    )
    add()

    # 7B
    add("### 7B. Engineering — Redundancy & Reliability")
    add()
    add(
        _safe_get(
            research, "section_7", "redundancy", default="*No research data available.*"
        )
    )
    add()

    # 7C
    add("### 7C. Ecology")
    add()
    s7 = research.get("section_7", {}) if isinstance(research, dict) else {}
    is_monopoly = s7.get("is_monopoly", False)
    monopoly_ev = s7.get("monopoly_evidence", "—")
    is_commodity = s7.get("is_commodity", False)
    commodity_ev = s7.get("commodity_evidence", "—")
    niche = s7.get("niche_defense", "—")
    add(
        "- **Monopoly/near-monopoly:** {} — {}".format(
            "✅" if is_monopoly else "❌", monopoly_ev
        )
    )
    add(
        "- **Commodity competition:** {} — {}".format(
            "✅" if is_commodity else "❌", commodity_ev
        )
    )
    add("- **Niche defense:** {}".format(niche))
    add()

    # 7D: Glotz Test
    add("### 7D. Glotz Test")
    add()
    add("- [{}] Elemental forces".format(check_mark(s7.get("glotz_elemental", False))))
    add("- [{}] Conditioned reflex".format(check_mark(s7.get("glotz_reflex", False))))
    add("- [{}] Social proof".format(check_mark(s7.get("glotz_social_proof", False))))
    add(
        "- [{}] No-brainer expansion".format(
            check_mark(s7.get("glotz_expansion", False))
        )
    )
    add()
    add("---")
    add()

    # ── Section 8: Lollapalooza Synthesis ──────────────────────────────────
    add("## Section 8: Lollapalooza Synthesis")
    add()
    signals = data.get("lolla_signals", [])
    if signals:
        add("**Auto-detected signals:** " + " · ".join(signals))
    else:
        add("**Auto-detected signals:** None detected")
    add()
    add(
        _safe_get(
            research, "section_8", "synthesis", default="*No research data available.*"
        )
    )
    add()
    add("---")
    add()

    # ── Section 9: Final Decision ──────────────────────────────────────────
    add("## Section 9: Final Decision & Recommendation")
    add()

    # 9A
    add("### 9A. Probability of Permanent Capital Loss")
    add()
    perm_pct = _safe_get(research, "section_9", "permanent_loss_pct", default="—")
    perm_rationale = _safe_get(
        research, "section_9", "permanent_loss_rationale", default="—"
    )
    add("**Estimated:** {}%".format(perm_pct))
    add()
    add(str(perm_rationale))
    add()

    # 9B: Checklist
    add("### 9B. Final Clearance Checklist")
    add()
    add("| Criterion | Verdict | Note |")
    add("|-----------|---------|------|")
    add(_checklist_row(research, "risk", "Risk"))
    add(_checklist_row(research, "independence", "Independence"))
    add(_checklist_row(research, "preparation", "Preparation"))
    add(_checklist_row(research, "humility", "Humility"))
    add(_checklist_row(research, "allocation", "Allocation"))
    add(_checklist_row(research, "patience", "Patience"))
    add(_checklist_row(research, "decisiveness", "Decisiveness"))
    add(_checklist_row(research, "disruption", "Disruption"))
    add(_checklist_row(research, "simplicity", "Simplicity"))
    add(_checklist_row(research, "rigor", "Rigor"))
    add()

    # 9C
    add("### 9C. Opportunity Cost")
    add()
    add(
        _safe_get(
            research,
            "section_9",
            "opportunity_cost",
            default="*No research data available.*",
        )
    )
    add()

    # 9D
    add("### 9D. FINAL DECISION")
    add()
    decision = _safe_get(research, "section_9", "final_decision", default="PENDING")
    final_rationale = _safe_get(research, "section_9", "final_rationale", default="—")
    add("## **{}**".format(decision))
    add()
    add(str(final_rationale))
    add()

    # ── Footer ─────────────────────────────────────────────────────────────
    add("---")
    add(
        "*Data: Yahoo Finance + Antigravity CLI Web Search | Date: {} | Ticker: {}*".format(
            report_date, ticker
        )
    )
    add()

    return "\n".join(lines)


# ===========================================================================
# CLI COMMANDS
# ===========================================================================


def cmd_list(args):
    """List tickers in an index."""
    tickers = get_index_tickers(args.index)
    print("Index: {} — {} tickers".format(args.index, len(tickers)))
    print("-" * 50)
    for i, tk in enumerate(tickers):
        print("  {:>3d}. {}".format(i + 1, tk))
    print("-" * 50)


def cmd_fetch(args):
    """Fetch financial data for tickers in an index."""
    output_dir = Path(args.output_dir)
    data_dir = output_dir / DATA_SUBDIR
    data_dir.mkdir(parents=True, exist_ok=True)

    # Fetch ticker list
    print("=" * 60)
    print("MUNGER CLI SCANNER — FETCH MODE")
    print("=" * 60)
    print("Index:            {}".format(args.index))
    print("Risk-Free Rate:   {}%".format(args.risk_free_rate))
    print("Maint CapEx Ratio: {}%".format(int(args.maint_capex_ratio * 100)))
    print("Output Directory: {}".format(output_dir.resolve()))
    print("-" * 60)
    print()

    print("Fetching ticker list for {} ...".format(args.index))
    tickers = get_index_tickers(args.index)
    print("  Found {} tickers.".format(len(tickers)))
    print()

    # Apply --start and --max
    start = args.start
    if start >= len(tickers):
        print(
            "ERROR: --start {} is beyond the ticker list (total: {})".format(
                start, len(tickers)
            )
        )
        return
    tickers = tickers[start:]
    if args.max is not None and args.max > 0:
        tickers = tickers[: args.max]

    print(
        "Processing {} tickers (start={}, max={})".format(
            len(tickers), start, args.max or "ALL"
        )
    )
    print("-" * 60)
    print()

    success = 0
    skipped = 0
    failed = 0

    for i, ticker in enumerate(tickers):
        idx_label = "[{}/{}]".format(i + 1, len(tickers))
        data_file = data_dir / "{}_data.json".format(ticker)

        # Skip if already fetched (unless --force)
        if data_file.exists() and not args.force:
            print("{} {} — SKIP (already fetched)".format(idx_label, ticker))
            skipped += 1
            continue

        print("{} {} — fetching ...".format(idx_label, ticker), end="")
        sys.stdout.flush()

        try:
            data = fetch_and_compute(
                ticker,
                risk_free_rate=args.risk_free_rate,
                maint_capex_ratio=args.maint_capex_ratio,
            )

            # Write JSON
            with open(str(data_file), "w", encoding="utf-8") as f:
                json.dump(data, f, indent=2, ensure_ascii=False, default=str)

            print(
                " OK  |  OE Yield: {:.1f}%  |  GM: {:.1f}%  |  MoS: {}".format(
                    data.get("oe_yield", 0),
                    data.get("latest_gm", 0),
                    data.get("mos_flag", "?")[:20],
                )
            )
            success += 1

        except Exception as e:
            print(" FAILED: {}".format(e))
            if args.verbose:
                traceback.print_exc()
            failed += 1

        # Brief delay to be respectful to Yahoo Finance
        time.sleep(3.0 if len(tickers) > 1000 else 0.5)

    # Summary
    print()
    print("=" * 60)
    print("FETCH COMPLETE")
    print(
        "  Success: {}  |  Skipped: {}  |  Failed: {}".format(success, skipped, failed)
    )
    print("  Data directory: {}".format(data_dir.resolve()))
    print("=" * 60)


def cmd_report(args):
    """Generate Markdown reports from data + research JSON."""
    output_dir = Path(args.output_dir)
    data_dir = output_dir / DATA_SUBDIR
    research_dir = output_dir / RESEARCH_SUBDIR
    reports_dir = output_dir / REPORTS_SUBDIR
    reports_dir.mkdir(parents=True, exist_ok=True)

    print("=" * 60)
    print("MUNGER CLI SCANNER — REPORT MODE")
    print("=" * 60)
    print()

    # Determine which tickers to report
    if args.all:
        # Find all data files
        if not data_dir.exists():
            print("ERROR: Data directory does not exist: {}".format(data_dir))
            return
        data_files = sorted(data_dir.glob("*_data.json"))
        tickers = [f.stem.replace("_data", "") for f in data_files]
        if not tickers:
            print("ERROR: No data files found in {}".format(data_dir))
            return
        print("Found {} data files.".format(len(tickers)))
    elif args.ticker:
        tickers = [args.ticker.upper()]
    else:
        print("ERROR: Specify --ticker AAPL or --all")
        return

    success = 0
    skipped = 0

    for i, ticker in enumerate(tickers):
        idx_label = "[{}/{}]".format(i + 1, len(tickers))
        data_file = data_dir / "{}_data.json".format(ticker)
        research_file = research_dir / "{}_research.json".format(ticker)

        # Check data file
        if not data_file.exists():
            print(
                "{} {} — SKIP (no data file: {})".format(
                    idx_label, ticker, data_file.name
                )
            )
            skipped += 1
            continue

        # Load data
        with open(str(data_file), "r", encoding="utf-8") as f:
            data = json.load(f)

        # Load research (optional — report renders with defaults if missing)
        if research_file.exists():
            with open(str(research_file), "r", encoding="utf-8") as f:
                research = json.load(f)
            has_research = True
        else:
            research = {}
            has_research = False

        # Generate report
        report_md = generate_report(data, research)

        # Write report file
        report_date = data.get("fetch_date", str(date.today()))
        report_filename = "{}_munger_{}.md".format(ticker, report_date)
        report_path = reports_dir / report_filename

        with open(str(report_path), "w", encoding="utf-8") as f:
            f.write(report_md)

        research_status = (
            "with research" if has_research else "DATA ONLY (no research JSON)"
        )
        print(
            "{} {} — OK ({}) -> {}".format(
                idx_label, ticker, research_status, report_filename
            )
        )
        success += 1

    # Summary
    print()
    print("=" * 60)
    print("REPORT COMPLETE")
    print("  Generated: {}  |  Skipped: {}".format(success, skipped))
    print("  Reports directory: {}".format(reports_dir.resolve()))
    print("=" * 60)


# ===========================================================================
# ARGUMENT PARSER
# ===========================================================================


def build_parser():
    """Build the argparse parser with subcommands."""
    parser = argparse.ArgumentParser(
        prog="munger_cli_scanner",
        description="Munger Protocol — CLI Index Scanner for batch stock analysis.",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog=(
            "Examples:\n"
            "  python munger_cli_scanner.py fetch --index DJIA --max 5\n"
            "  python munger_cli_scanner.py report --ticker AAPL\n"
            "  python munger_cli_scanner.py report --all\n"
            "  python munger_cli_scanner.py list --index SP500\n"
        ),
    )

    subparsers = parser.add_subparsers(dest="command", help="Available commands")

    # ── fetch ──────────────────────────────────────────────────────────────
    p_fetch = subparsers.add_parser(
        "fetch",
        help="Fetch yfinance data and compute metrics for an index",
    )
    p_fetch.add_argument(
        "--index",
        default="SP500",
        choices=["DJIA", "SP500", "NASDAQ100", "RUSSELL3000E"],
        help="Stock index to scan",
    )
    p_fetch.add_argument(
        "--max",
        type=int,
        default=None,
        help="Maximum number of tickers to process (default: all)",
    )
    p_fetch.add_argument(
        "--start",
        type=int,
        default=0,
        help="Starting index in the ticker list (default: 0)",
    )
    p_fetch.add_argument(
        "--risk-free-rate",
        type=float,
        default=4.35,
        help="Risk-free rate in %% (default: 4.35)",
    )
    p_fetch.add_argument(
        "--maint-capex-ratio",
        type=float,
        default=0.70,
        help="Maintenance CapEx as fraction of total CapEx (default: 0.70)",
    )
    p_fetch.add_argument(
        "--force",
        action="store_true",
        help="Re-fetch tickers that already have data files",
    )
    p_fetch.add_argument(
        "--verbose",
        action="store_true",
        help="Print full tracebacks on errors",
    )
    p_fetch.add_argument(
        "--output-dir",
        default=DEFAULT_OUTPUT_DIR,
        help="Output directory (default: {})".format(DEFAULT_OUTPUT_DIR),
    )

    # ── report ─────────────────────────────────────────────────────────────
    p_report = subparsers.add_parser(
        "report",
        help="Generate Markdown reports from data + research JSON",
    )
    report_group = p_report.add_mutually_exclusive_group(required=True)
    report_group.add_argument(
        "--ticker",
        type=str,
        help="Single ticker to generate a report for",
    )
    report_group.add_argument(
        "--all",
        action="store_true",
        help="Generate reports for all tickers with data files",
    )
    p_report.add_argument(
        "--output-dir",
        default=DEFAULT_OUTPUT_DIR,
        help="Output directory (default: {})".format(DEFAULT_OUTPUT_DIR),
    )

    # ── list ───────────────────────────────────────────────────────────────
    p_list = subparsers.add_parser(
        "list",
        help="List tickers in a stock index",
    )
    p_list.add_argument(
        "--index",
        default="SP500",
        choices=["DJIA", "SP500", "NASDAQ100", "RUSSELL3000E"],
        help="Stock index to list",
    )

    return parser


# ===========================================================================
# ENTRY POINT
# ===========================================================================


def main():
    parser = build_parser()
    args = parser.parse_args()

    if args.command is None:
        parser.print_help()
        sys.exit(1)

    if args.command == "list":
        cmd_list(args)
    elif args.command == "fetch":
        cmd_fetch(args)
    elif args.command == "report":
        cmd_report(args)
    else:
        parser.print_help()
        sys.exit(1)


if __name__ == "__main__":
    main()

# Munger Protocol: Python Pipeline Documentation

This document outlines the purpose, functionality, and execution order of the Python scripts used to power the Munger Stock Analysis Pipeline. The system relies on two primary data engines (for hard financials and qualitative research) and several lightweight extraction scripts to filter the resulting database.

---

## 1. The Core Engines

These two scripts are responsible for gathering all raw data from the internet, performing the complex financial math, and rendering the final Markdown reports.

### `munger_cli_scanner.py`
**The Data Fetcher & Report Generator**
*   **Dependencies:** `yfinance`, `argparse`, `json`, `os`
*   **Functionality:** This is the absolute core of the project. It connects directly to the Yahoo Finance API (`yfinance`) to download 5 years of historical financial statements (Income, Balance Sheet, and Cash Flow). It then calculates strict Charlie Munger metrics, including Owner Earnings, Maintenance Capex ratios, and Return on Equity (ROE). 
*   **CLI Usage:** 
    *   `python munger_cli_scanner.py fetch --index RUSSELL3000E` (Downloads data to `munger_output/data/`)
    *   `python munger_cli_scanner.py report --ticker AAPL` (Generates a final `.md` report using the data)

### `fill_research.py`
**The Qualitative Web Scraper**
*   **Dependencies:** `duckduckgo_search`, `json`, `glob`
*   **Functionality:** Once the financial data is downloaded, `munger_cli_scanner.py` generates specific qualitative questions based on the financial results (e.g., searching for CEO compensation details or analyzing a company's competitive moat). This script crawls the web using the DuckDuckGo Search API to find answers to those questions.
*   **Output:** It saves the text summaries into `munger_output/research/*_research.json` so they can be injected into the final Markdown reports.

---

## 2. The Extraction & Filtering Scripts

Once the massive database of 3,500+ JSON files is built locally, these lightweight scripts act as filters to comb through the data and rank the best investment opportunities based on different Munger criteria.

### `extract_high_yield.py`
*   **Purpose:** The baseline filter. 
*   **Functionality:** Iterates through every JSON data file and extracts any company with an Owner Earnings Yield $\geq$ 7%.
*   **Output:** Generates `high_yield_stocks.md`.

### `extract_sustainable_yield.py`
*   **Purpose:** The "Value Trap" eliminator.
*   **Functionality:** A stricter version of the high yield script. It caps the Owner Earnings Yield at 15% (filtering out 7% to 15%). This safely removes companies with statistical anomalies, cyclical peaks, or terminal decline that artificially inflate their yield.
*   **Output:** Generates `sustainable_yield_stocks.md` (Top Section).

### `extract_great_businesses.py`
*   **Purpose:** The "Great Business at a Fair Price" finder.
*   **Functionality:** Looks for companies trading at a premium (2% to 7% Owner Earnings Yield) but forces them to justify that premium by passing incredibly strict quality filters:
    *   Gross Margin $\geq$ 50% (Pricing Power)
    *   Return on Equity $\geq$ 20% (Compounding Efficiency)
    *   Debt/Equity $\leq$ 1.0 (Low Leverage)
*   **Output:** Appends the elite winners to the bottom of `sustainable_yield_stocks.md`.

### `extract_gems.py`
*   **Purpose:** The "Hidden Gem" filter.
*   **Functionality:** An alternative multi-factor script designed to find companies that offer both a high margin of safety and a strong moat simultaneously (Yield $\geq$ 7%, Gross Margin $\geq$ 40%, Debt/Equity $\leq$ 0.5).

---

## Recommended Execution Flow

To refresh the entire database from scratch, the scripts should be run in the following order:

1. `python munger_cli_scanner.py fetch --index RUSSELL3000E` *(Downloads financials)*
2. `python fill_research.py` *(Gathers web research)*
3. `python extract_high_yield.py` *(Builds baseline list)*
4. `python extract_sustainable_yield.py` *(Filters for safety)*
5. `python extract_great_businesses.py` *(Finds premium compounders)*
6. `python munger_cli_scanner.py report --ticker [TICKER]` *(Generates deep-dive report for chosen winner)*

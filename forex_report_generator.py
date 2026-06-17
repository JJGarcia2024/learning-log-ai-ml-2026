import yfinance as yf
import pandas as pd
import numpy as np
from datetime import datetime, timedelta
import os
import urllib.request
import urllib.parse
import xml.etree.ElementTree as ET
def calculate_indicators(df):
    """Calculates necessary technical indicators for the strategy."""
    df['EMA_9'] = df['Close'].ewm(span=9, adjust=False).mean()
    df['EMA_21'] = df['Close'].ewm(span=21, adjust=False).mean()
    df['EMA_50'] = df['Close'].ewm(span=50, adjust=False).mean()
    df['EMA_200'] = df['Close'].ewm(span=200, adjust=False).mean()
    
    delta = df['Close'].diff()
    gain = (delta.where(delta > 0, 0)).rolling(window=14).mean()
    loss = (-delta.where(delta < 0, 0)).rolling(window=14).mean()
    rs = gain / loss
    df['RSI'] = 100 - (100 / (1 + rs))
    
    high_low = df['High'] - df['Low']
    high_close = np.abs(df['High'] - df['Close'].shift())
    low_close = np.abs(df['Low'] - df['Close'].shift())
    ranges = pd.concat([high_low, high_close, low_close], axis=1)
    true_range = np.max(ranges, axis=1)
    df['ATR'] = true_range.rolling(14).mean()
    
    return df

def get_fundamental_data(symbol):
    """
    Scours the web (via Google News RSS) for recent macroeconomic news related to the currencies.
    Filters for news within the last 30 days. Fallbacks to yfinance if no results found.
    """
    clean_sym = symbol.replace('=X', '')
    if len(clean_sym) == 6:
        base = clean_sym[:3]
        quote = clean_sym[3:]
    else:
        base = clean_sym
        quote = 'economy'
        
    names = {
        'USD': 'US Dollar', 'EUR': 'Euro', 'GBP': 'British Pound', 
        'JPY': 'Japanese Yen', 'AUD': 'Australian Dollar', 'NZD': 'New Zealand Dollar',
        'CAD': 'Canadian Dollar', 'CHF': 'Swiss Franc', 
        'XAU': 'Gold', 'XAG': 'Silver', 'XPT': 'Platinum', 'XPD': 'Palladium',
        'GC=F': 'Gold', 'SI=F': 'Silver', 'PL=F': 'Platinum', 'PA=F': 'Palladium'
    }
    
    base_name = names.get(base, base)
    quote_name = names.get(quote, quote)
    
    query = f'"{base_name}" "{quote_name}" (forex OR economy OR central bank)'
    if base == quote or quote == 'economy':
        query = f'"{base_name}" (forex OR economy OR central bank)'
        
    encoded_query = urllib.parse.quote(query)
    url = f'https://news.google.com/rss/search?q={encoded_query}&hl=en-US&gl=US&ceid=US:en'
    
    parsed_news = []
    
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req, timeout=10) as response:
            xml_data = response.read()
            root = ET.fromstring(xml_data)
            items = root.findall('.//item')
            
            thirty_days_ago = datetime.now() - timedelta(days=30)
            
            for item in items:
                title = item.find('title').text
                pubDate_str = item.find('pubDate').text
                source = item.find('source').text if item.find('source') is not None else 'News'
                
                try:
                    pubDate = datetime.strptime(pubDate_str, '%a, %d %b %Y %H:%M:%S %Z')
                    if pubDate < thirty_days_ago:
                        continue
                    date_display = pubDate.strftime('%Y-%m-%d')
                except Exception:
                    date_display = pubDate_str[:16]
                
                parsed_news.append({'title': title, 'publisher': source, 'date': date_display})
                if len(parsed_news) >= 10:
                    break
    except Exception as e:
        print(f"Error fetching RSS news for {symbol}: {e}")
        
    if not parsed_news:
        try:
            ticker_obj = yf.Ticker(symbol)
            news = ticker_obj.news
            for item in (news[:10] if news else []):
                if 'content' in item:
                    content = item['content']
                    title = content.get('title', 'No Title provided.')
                    provider = content.get('provider', {}).get('displayName', 'Unknown')
                    parsed_news.append({'title': title, 'publisher': provider, 'date': 'Recent'})
                else:
                    title = item.get('title', 'No Title provided.')
                    provider = item.get('publisher', 'Unknown')
                    parsed_news.append({'title': title, 'publisher': provider, 'date': 'Recent'})
        except Exception:
            pass
            
    return parsed_news

def generate_report(symbol, account_balance, risk_pct=1.0, interval='1d'):
    """Generates a comprehensive trading report based on the template and saves it as Markdown."""
    ticker = yf.Ticker(symbol)
    
    df = ticker.history(period='1y', interval=interval)
    if df.empty:
        print(f"Failed to fetch data for {symbol}")
        return None
    
    df = calculate_indicators(df)
    latest = df.iloc[-1]
    prev = df.iloc[-2]
    
    current_price = latest['Close']
    
    signal = "NO ENTRY (WAIT)"
    reason = "No clear setup based on system parameters. Wait for high-probability alignment."
    
    uptrend = current_price > latest['EMA_50'] and latest['EMA_50'] > latest['EMA_200']
    downtrend = current_price < latest['EMA_50'] and latest['EMA_50'] < latest['EMA_200']
    regime = 'UPTREND (Momentum Continuation)' if uptrend else 'DOWNTREND (Momentum Continuation)' if downtrend else 'RANGING/COMPRESSION (Mean-Reversion)'
    
    if uptrend and latest['EMA_9'] > latest['EMA_21'] and prev['EMA_9'] <= prev['EMA_21']:
        signal = "BUY"
        reason = "Bullish EMA 9/21 Crossover aligned with broader Uptrend."
    elif downtrend and latest['EMA_9'] < latest['EMA_21'] and prev['EMA_9'] >= prev['EMA_21']:
        signal = "SELL"
        reason = "Bearish EMA 9/21 Crossover aligned with broader Downtrend."
        
    if signal == "NO ENTRY (WAIT)":
        if latest['RSI'] < 30 and current_price > latest['EMA_200']:
            signal = "BUY"
            reason = "RSI Oversold (<30) Pullback in broader Uptrend regime."
        elif latest['RSI'] > 70 and current_price < latest['EMA_200']:
            signal = "SELL"
            reason = "RSI Overbought (>70) Rally in broader Downtrend regime."

    # --- Always-on Directional Bias (for new entries with no open trade) ---
    if uptrend:
        if latest['RSI'] >= 70:
            bias = "BUY (CAUTION \u2014 RSI Overbought, consider waiting for a pullback)"
            bias_emoji = "\U0001f7e1"  # yellow circle
        else:
            bias = "BUY"
            bias_emoji = "\U0001f7e2"  # green circle
    elif downtrend:
        if latest['RSI'] <= 30:
            bias = "SELL (CAUTION \u2014 RSI Oversold, consider waiting for a bounce)"
            bias_emoji = "\U0001f7e1"
        else:
            bias = "SELL"
            bias_emoji = "\U0001f534"  # red circle
    else:  # Ranging / Compression
        if latest['RSI'] > 60:
            bias = "SELL (Mean-Reversion from upper range)"
            bias_emoji = "\U0001f534"
        elif latest['RSI'] < 40:
            bias = "BUY (Mean-Reversion from lower range)"
            bias_emoji = "\U0001f7e2"
        else:
            bias = "NEUTRAL \u2014 Wait for a clear range breakout"
            bias_emoji = "\u26aa"  # white/grey circle

    # --- Trade Management for Existing Positions ---
    ema9_crossed_below = latest['EMA_9'] < latest['EMA_21'] and prev['EMA_9'] >= prev['EMA_21']
    ema9_crossed_above = latest['EMA_9'] > latest['EMA_21'] and prev['EMA_9'] <= prev['EMA_21']

    if uptrend and latest['EMA_9'] > latest['EMA_21'] and latest['RSI'] < 70:
        hold_buy = "\u2705 HOLD \u2014 Uptrend intact, momentum and RSI support continuation."
    elif downtrend:
        hold_buy = "\U0001f534 CLOSE BUY \u2014 Trend reversed to Downtrend. Exit to protect capital."
    elif ema9_crossed_below:
        hold_buy = "\u26a0\ufe0f CONSIDER CLOSING \u2014 EMA 9 just crossed below EMA 21, momentum fading."
    elif latest['RSI'] > 75:
        hold_buy = "\u26a0\ufe0f CONSIDER PARTIAL CLOSE \u2014 RSI extreme overbought (>75), reversal risk elevated."
    else:
        hold_buy = "\U0001f7e1 MONITOR \u2014 Mixed signals. Trail stop-loss and wait for clarity."

    if downtrend and latest['EMA_9'] < latest['EMA_21'] and latest['RSI'] > 30:
        hold_sell = "\u2705 HOLD \u2014 Downtrend intact, momentum and RSI support continuation."
    elif uptrend:
        hold_sell = "\U0001f534 CLOSE SELL \u2014 Trend reversed to Uptrend. Exit to protect capital."
    elif ema9_crossed_above:
        hold_sell = "\u26a0\ufe0f CONSIDER CLOSING \u2014 EMA 9 just crossed above EMA 21, momentum fading."
    elif latest['RSI'] < 25:
        hold_sell = "\u26a0\ufe0f CONSIDER PARTIAL CLOSE \u2014 RSI extreme oversold (<25), reversal risk elevated."
    else:
        hold_sell = "\U0001f7e1 MONITOR \u2014 Mixed signals. Trail stop-loss and wait for clarity."

    risk_amount = account_balance * (risk_pct / 100)
    atr = latest['ATR']
    
    if signal == "BUY":
        stop_loss = current_price - (1.5 * atr) 
        take_profit = current_price + (3.0 * atr) 
    elif signal == "SELL":
        stop_loss = current_price + (1.5 * atr)
        take_profit = current_price - (3.0 * atr) 
    else:
        # Theoretical stops for context even when waiting
        stop_loss = current_price - (1.5 * atr)
        take_profit = current_price + (3.0 * atr)

    risk_per_unit = abs(current_price - stop_loss)
    if risk_per_unit > 0:
        units = risk_amount / risk_per_unit
        lot_size = units / 100000 
        xm_micro_lots = units / 1000
    else:
        lot_size = 0
        xm_micro_lots = 0

    news_items = get_fundamental_data(symbol)

    # Markdown Content Generation
    md = f"# Systematic Trading Report: {symbol}\n\n"
    md += f"**Generated:** {datetime.now().strftime('%Y-%m-%d %H:%M')} | **Timeframe:** {interval}\n\n"
    
    md += "## 1. Macroeconomic Context & Fundamentals\n"
    if news_items:
        for i, article in enumerate(news_items, 1):
            title = article.get('title', 'No Title provided.')
            publisher = article.get('publisher', 'Unknown')
            date_str = article.get('date', '')
            md += f"- **{date_str} ({publisher}):** {title}\n"
    else:
        md += "- No tier-one news or fundamental data detected immediately via API.\n"
        
    md += "\n## 2. Technical Architecture & Regime\n"
    md += f"- **Current Price:** {current_price:.5f}\n"
    md += f"- **Regime/Trend:** {regime}\n"
    md += f"- **EMA 50 (Med):** {latest['EMA_50']:.5f}\n"
    md += f"- **EMA 200 (Long):** {latest['EMA_200']:.5f}\n"
    md += f"- **RSI (14):** {latest['RSI']:.2f} (Overbought >70, Oversold <30)\n"
    md += f"- **ATR (14):** {latest['ATR']:.5f} (Current Volatility)\n"
    
    md += "\n## 3. Execution Criteria\n"

    md += "### 3a. Trigger Signal (Specific System Entry)\n"
    if signal == "NO ENTRY (WAIT)":
        md += f"**ACTION SIGNAL:** \u23f8\ufe0f **{signal}**\n\n"
    elif signal == "BUY":
        md += f"**ACTION SIGNAL:** \U0001f7e2 **{signal}**\n\n"
    elif signal == "SELL":
        md += f"**ACTION SIGNAL:** \U0001f534 **{signal}**\n\n"
    md += f"**JUSTIFICATION:** {reason}\n\n"

    md += "### 3b. Directional Bias (No Open Trade)\n"
    md += f"**BIAS:** {bias_emoji} **{bias}**\n"
    md += "> *Reflects current trend & momentum. Only enter when your system trigger fires (3a above).*\n\n"

    md += "### 3c. Trade Management (Existing Open Trade)\n"
    md += "| Existing Position | Recommendation |\n"
    md += "|---|---|\n"
    md += f"| \U0001f4c8 **Holding a BUY** | {hold_buy} |\n"
    md += f"| \U0001f4c9 **Holding a SELL** | {hold_sell} |\n"
    
    md += "\n## 4. Risk & Portfolio Defense\n"
    md += f"- **Capital Base:** ${account_balance:,.2f}\n"
    md += f"- **Risk Limit:** {risk_pct}% / Risking strictly ${risk_amount:,.2f}\n"
    
    if signal == "NO ENTRY (WAIT)":
        md += f"\n> **Theoretical Sizing (If a setup was present):**\n"
    else:
        md += f"- **Stop Loss (SL):** {stop_loss:.5f}\n"
        md += f"- **Take Profit (TP):** {take_profit:.5f} (Targeting 1:2 RRR)\n"
        
    md += f"- **Standard Lot Size:** {lot_size:.3f} Lots (100,000 units)\n"
    md += f"- **XM Micro Account Lot Size:** {xm_micro_lots:.3f} Micro Lots (1,000 units)\n"
    md += "> *Note: XM Micro Accounts require a minimum deposit of just $5 and help manage risk. Adjust sizing dynamically for pairs with varying pip values.*\n"
    
    md += "\n## 5. Pre-Trade Diagnostic Check\n"
    if signal != "NO ENTRY (WAIT)":
        md += "- [ ] Is this setup identical to the trading plan?\n"
        md += "- [ ] Is emotional equilibrium met (No FOMO/Revenge)?\n"
        md += "- [ ] Are there any conflicting High-Impact News events shortly?\n"
    else:
        md += "- [x] System invalidated setup. Capital preserved.\n"
        
    md += "\n---\n*Post-Trade: Document execution in journal regardless of outcome.*\n"

    # Save to file
    if not os.path.exists('reports'):
        os.makedirs('reports')
    filename = f"reports/Report_{symbol.replace('=X', '')}.md"
    with open(filename, 'w', encoding='utf-8') as f:
        f.write(md)
        
    print(f"Generated Markdown report: {filename}")
    return {'symbol': symbol.replace('=X', ''), 'lots': xm_micro_lots}


if __name__ == "__main__":
    ACCOUNT_BALANCE = 10.0     # $10 Micro-Lot Account
    RISK_PERCENTAGE = 1.0      
    
    ASSETS = [
        'AUDCAD=X', 'AUDCHF=X', 'AUDJPY=X', 'AUDNZD=X', 'AUDUSD=X',
        'CADCHF=X', 'CADJPY=X', 'CHFJPY=X',
        'EURAUD=X', 'EURCAD=X', 'EURCHF=X', 'EURGBP=X', 'EURJPY=X', 'EURNZD=X', 'EURTRY=X', 'EURUSD=X', 'EURZAR=X',
        'GBPAUD=X', 'GBPCAD=X', 'GBPCHF=X', 'GBPJPY=X', 'GBPNZD=X', 'GBPUSD=X', 'GBPZAR=X',
        'NZDCAD=X', 'NZDCHF=X', 'NZDJPY=X', 'NZDUSD=X',
        'USDCAD=X', 'USDCHF=X', 'USDDKK=X', 'USDHKD=X', 'USDINR=X', 'USDJPY=X',
        'USDMXN=X', 'USDNOK=X', 'USDPLN=X', 'USDSEK=X', 'USDSGD=X', 'USDTHB=X', 'USDTRY=X', 'USDZAR=X'
    ]
    
    tradeable_pairs = []
    
    for asset in ASSETS:
        result = generate_report(asset, ACCOUNT_BALANCE, risk_pct=RISK_PERCENTAGE, interval='1d')
        if result and result['lots'] >= 0.01:
            tradeable_pairs.append(result)
            
    # Write summary document
    tradeable_pairs.sort(key=lambda x: x['lots'], reverse=True)
    
    summary_md = "# Tradeable Forex Universe (>= 0.01 Micro Lots)\n\n"
    summary_md += f"**Generated:** {datetime.now().strftime('%Y-%m-%d %H:%M')}\n"
    summary_md += f"**Parameters:** ${ACCOUNT_BALANCE} Account | {RISK_PERCENTAGE}% Risk\n\n"
    summary_md += "| Pair | Allowable XM Micro Lots |\n"
    summary_md += "|---|---|\n"
    
    for item in tradeable_pairs:
        summary_md += f"| **{item['symbol']}** | {item['lots']:.3f} |\n"
        
    if not tradeable_pairs:
        summary_md += "| *None* | *N/A* |\n"
        
    if not os.path.exists('reports'):
        os.makedirs('reports')
        
    with open('reports/Tradeable_Forex_Universe.md', 'w', encoding='utf-8') as f:
        f.write(summary_md)
        
    print(f"Generated summary: reports/Tradeable_Forex_Universe.md with {len(tradeable_pairs)} pairs.")

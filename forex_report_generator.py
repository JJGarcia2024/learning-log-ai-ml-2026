import yfinance as yf
import pandas as pd
import numpy as np
from datetime import datetime
import os

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

def get_fundamental_data(ticker_obj):
    """Fetches top 10 recent news items for the ticker."""
    try:
        news = ticker_obj.news
        parsed_news = []
        for item in (news[:10] if news else []):
            if 'content' in item:
                content = item['content']
                title = content.get('title', 'No Title provided.')
                provider = content.get('provider', {}).get('displayName', 'Unknown')
                parsed_news.append({'title': title, 'publisher': provider})
            else:
                title = item.get('title', 'No Title provided.')
                provider = item.get('publisher', 'Unknown')
                parsed_news.append({'title': title, 'publisher': provider})
        return parsed_news
    except Exception:
        return []

def generate_report(symbol, account_balance, risk_pct=1.0, interval='1d'):
    """Generates a comprehensive trading report based on the template and saves it as Markdown."""
    ticker = yf.Ticker(symbol)
    
    df = ticker.history(period='1y', interval=interval)
    if df.empty:
        print(f"Failed to fetch data for {symbol}")
        return
    
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

    news_items = get_fundamental_data(ticker)

    # Markdown Content Generation
    md = f"# Systematic Trading Report: {symbol}\n\n"
    md += f"**Generated:** {datetime.now().strftime('%Y-%m-%d %H:%M')} | **Timeframe:** {interval}\n\n"
    
    md += "## 1. Macroeconomic Context & Fundamentals\n"
    if news_items:
        for i, article in enumerate(news_items, 1):
            title = article.get('title', 'No Title provided.')
            publisher = article.get('publisher', 'Unknown')
            md += f"- **NEWS {i} ({publisher}):** {title}\n"
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
    if signal == "NO ENTRY (WAIT)":
        md += f"**ACTION SIGNAL:** ⏸️ **{signal}**\n\n"
    elif signal == "BUY":
        md += f"**ACTION SIGNAL:** 🟢 **{signal}**\n\n"
    elif signal == "SELL":
        md += f"**ACTION SIGNAL:** 🔴 **{signal}**\n\n"
        
    md += f"**JUSTIFICATION:** {reason}\n"
    
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


if __name__ == "__main__":
    ACCOUNT_BALANCE = 10.0     # $10 Micro-Lot Account
    RISK_PERCENTAGE = 1.0      
    
    ASSETS = ['EURUSD=X']
    
    for asset in ASSETS:
        generate_report(asset, ACCOUNT_BALANCE, risk_pct=RISK_PERCENTAGE, interval='1d')

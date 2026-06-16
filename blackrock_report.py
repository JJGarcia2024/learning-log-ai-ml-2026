import json

tickers = ['BBN', 'BCAT', 'BCX', 'BDJ', 'BGR', 'BGT', 'BGY', 'BHK', 'BIT', 'BKT', 'BLK', 'BLW', 'BME', 'BOE', 'BSTZ', 'BST', 'BTT', 'BTX', 'BTZ', 'BUI', 'CII', 'DSU', 'ECAT', 'FRA', 'HYT', 'MHD', 'MIY', 'MQY', 'MUA', 'MUC', 'MUJ', 'MYI', 'MYN', 'TCPC']

lines = []
for t in tickers:
    path = f'munger_output/data/{t}_data.json'
    try:
        with open(path, 'r') as file:
            d = json.load(file)
            name = d.get('company_name', '')
            y = d.get('oe_yield')
            gm = d.get('latest_gm')
            de = d.get('debt_equity')
            y_str = f'{y:.2f}%' if y is not None else 'N/A'
            gm_str = f'{gm:.1f}%' if gm is not None else 'N/A'
            de_str = f'{de:.2f}' if de is not None else 'N/A'
            
            reason = []
            if y is None or not (7 <= y <= 15): reason.append('Yield not 7-15%')
            if gm is None or gm < 40: reason.append('GM < 40%')
            if de is None or de > 0.5: reason.append('D/E > 0.5')
            
            reason_str = ', '.join(reason)
            lines.append(f'| {t} | {name} | {y_str} | {gm_str} | {de_str} | {reason_str} |')
    except Exception:
        pass

lines.sort()
out = ['| Ticker | Company Name | Yield | GM | D/E | Failure Reason |']
out.append('|---|---|---|---|---|---|')
out.extend(lines)

with open('blackrock_report.md', 'w') as f:
    f.write('\n'.join(out))

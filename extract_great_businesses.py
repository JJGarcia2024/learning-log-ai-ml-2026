import glob
import json
import os

res = []
for f in glob.glob('munger_output/data/*_data.json'):
    try:
        with open(f, 'r') as file:
            d = json.load(file)
            y = d.get('oe_yield')
            gm = d.get('latest_gm')
            roe = d.get('roe')
            de = d.get('debt_equity')
            
            # Great Business filter: 2% <= OE Yield < 7%, GM >= 50%, ROE >= 20%, DE <= 1.0
            if y is not None and 2 <= y < 7:
                if gm is not None and gm >= 50:
                    if roe is not None and roe >= 20:
                        if de is not None and de <= 1.0:
                            res.append((
                                d.get('ticker', 'Unknown'), 
                                d.get('company_name', 'Unknown'), 
                                y, gm, roe
                            ))
    except Exception as e:
        pass

# Sort by ROE descending
res.sort(key=lambda x: x[4], reverse=True)

lines = [
    '',
    '## Great Businesses at a Fair Price (The Munger Premium)',
    '> **Filters Applied:** 2% $\leq$ OE Yield < 7%, Gross Margin $\geq$ 50%, ROE $\geq$ 20%, Debt/Equity $\leq$ 1.0',
    '>',
    '> *These companies are priced at a premium (lower yield), but their exceptional gross margins and extremely high returns on equity indicate they have massive pricing power, impenetrable moats, and the ability to compound capital over the long term. They are the definition of "a great business at a fair price."*',
    '',
    '| Ticker | Company Name | OE Yield | Gross Margin | ROE |',
    '|--------|--------------|----------|--------------|-----|'
]

for t, n, y, gm, roe in res:
    lines.append(f'| {t} | {n} | {y:.2f}% | {gm:.1f}% | {roe:.1f}% |')

out_path = 'sustainable_yield_stocks.md'
with open(out_path, 'a') as file:
    file.write('\n'.join(lines))

print(f'Appended {len(res)} great businesses to {out_path}.')

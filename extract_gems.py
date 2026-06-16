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
            de = d.get('debt_equity')
            
            # Filters: OE Yield >= 7%, Gross Margin >= 40%, Debt/Equity <= 0.5
            if y is not None and y >= 7:
                if gm is not None and gm >= 40:
                    if de is not None and de <= 0.5:
                        res.append((
                            d.get('ticker', 'Unknown'), 
                            d.get('company_name', 'Unknown'), 
                            y, gm, de
                        ))
    except Exception as e:
        pass

# Sort by OE Yield descending
res.sort(key=lambda x: x[2], reverse=True)

lines = [
    '# Munger Protocol: The Hidden Gems',
    '> **Filters Applied:** Owner Earnings Yield $\geq$ 7%, Gross Margin $\geq$ 40%, Debt-to-Equity $\leq$ 0.5',
    '',
    '| Ticker | Company Name | OE Yield | Gross Margin | D/E Ratio |',
    '|--------|--------------|----------|--------------|-----------|'
]

for t, n, y, gm, de in res:
    lines.append(f'| {t} | {n} | {y:.2f}% | {gm:.1f}% | {de:.2f}x |')

out_path = 'hidden_gems.md'
with open(out_path, 'w') as file:
    file.write('\n'.join(lines))

print(f'Generated {out_path} with {len(res)} absolute gem stocks.')

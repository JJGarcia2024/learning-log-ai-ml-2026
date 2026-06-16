import glob
import json
import os

res = []
for f in glob.glob('munger_output/data/*_data.json'):
    try:
        with open(f, 'r') as file:
            d = json.load(file)
            y = d.get('oe_yield')
            
            # Sustainable Yield filter: 7% <= OE Yield <= 15%
            if y is not None and 7 <= y <= 15:
                res.append((
                    d.get('ticker', 'Unknown'), 
                    d.get('company_name', 'Unknown'), 
                    y
                ))
    except Exception as e:
        pass

# Sort by OE Yield descending
res.sort(key=lambda x: x[2], reverse=True)

lines = [
    '# Munger Protocol: The Sustainable Sweet Spot',
    '> **Filters Applied:** 7% $\leq$ Owner Earnings Yield $\leq$ 15%',
    '>',
    '> *These companies are in the historical "goldilocks zone" — undervalued enough to offer a significant margin of safety, but not so cheap that they trigger immediate "value trap" warnings.*',
    '',
    '| Ticker | Company Name | OE Yield |',
    '|--------|--------------|----------|'
]

for t, n, y in res:
    lines.append(f'| {t} | {n} | {y:.2f}% |')

out_path = 'sustainable_yield_stocks.md'
with open(out_path, 'w') as file:
    file.write('\n'.join(lines))

print(f'Generated {out_path} with {len(res)} sustainable yield stocks.')

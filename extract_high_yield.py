import glob
import json
import os

res = []
for f in glob.glob('munger_output/data/*_data.json'):
    try:
        with open(f, 'r') as file:
            d = json.load(file)
            y = d.get('oe_yield')
            if y is not None and y >= 7:
                res.append((d.get('ticker', 'Unknown'), d.get('company_name', 'Unknown'), y))
    except Exception as e:
        pass

res.sort(key=lambda x: x[2], reverse=True)

lines = [
    '# Stocks with OE Yield >= 7%',
    '',
    '| Ticker | Company Name | OE Yield |',
    '|--------|--------------|----------|'
]

for t, n, y in res:
    lines.append(f'| {t} | {n} | {y:.2f}% |')

out_path = 'high_yield_stocks.md'
with open(out_path, 'w') as file:
    file.write('\n'.join(lines))

print(f'Generated {out_path} with {len(res)} stocks.')

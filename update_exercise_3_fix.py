import json

notebook_path = r"G:\My Drive\Spacesmith and Wordsmith's Tower\Spacesmith's HQ\Nuclear Energy and Propulsion Engineering\Accenture AI-ML Computational Scientist\GitHub\learning-log-ai-ml-2026\joining_data_exercise.ipynb"

with open(notebook_path, 'r', encoding='utf-8') as f:
    nb = json.load(f)

# Find the cell we just added and fix the to_dataframe line
for cell in nb['cells']:
    if cell.get('id') == 'q3_exercise' or any('questions_results = questions_query_job.to_dataframe()' in line for line in cell['source']):
        new_source = []
        for line in cell['source']:
            if 'questions_results = questions_query_job.to_dataframe()' in line:
                new_source.append(line.replace(
                    'questions_query_job.to_dataframe()', 
                    'questions_query_job.to_dataframe(create_bqstorage_client=False)'
                ))
            else:
                new_source.append(line)
        cell['source'] = new_source

with open(notebook_path, 'w', encoding='utf-8') as f:
    json.dump(nb, f, indent=1)

print("Notebook joining_data_exercise.ipynb fixed.")

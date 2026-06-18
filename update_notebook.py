import json
import os

notebook_path = r"G:\My Drive\Spacesmith and Wordsmith's Tower\Spacesmith's HQ\Nuclear Energy and Propulsion Engineering\Accenture AI-ML Computational Scientist\GitHub\learning-log-ai-ml-2026\joining_data.ipynb"

with open(notebook_path, 'r', encoding='utf-8') as f:
    nb = json.load(f)

# Option 1 code to insert
option_1_code = [
    "from google.cloud import bigquery\n",
    "\n",
    "# Create a \"Client\" object\n",
    "client = bigquery.Client()\n",
    "\n",
    "# Construct a reference to the \"github_repos\" dataset\n",
    "dataset_ref = client.dataset(\"github_repos\", project=\"bigquery-public-data\")\n",
    "\n",
    "# API request - fetch the dataset\n",
    "dataset = client.get_dataset(dataset_ref)\n",
    "\n",
    "# Construct a reference to the \"sample_files\" table\n",
    "files_ref = dataset_ref.table(\"sample_files\")\n",
    "\n",
    "# API request - fetch the table\n",
    "files_table = client.get_table(files_ref)\n",
    "\n",
    "# Preview the first five lines of the \"sample_files\" table\n",
    "client.list_rows(files_table, max_results=5).to_dataframe()"
]

# We want to replace the 3rd and 4th cell (index 2 and 3) with a single new cell
# Cell 2 is currently the dataset loading, Cell 3 is the table loading
if len(nb['cells']) > 3:
    # Update Cell 2
    nb['cells'][2]['source'] = option_1_code
    nb['cells'][2]['outputs'] = []
    nb['cells'][2]['execution_count'] = None
    
    # Remove Cell 3
    del nb['cells'][3]

with open(notebook_path, 'w', encoding='utf-8') as f:
    json.dump(nb, f, indent=1)

print("Notebook updated successfully.")

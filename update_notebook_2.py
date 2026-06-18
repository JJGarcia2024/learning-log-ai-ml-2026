import json
import os

notebook_path = r"G:\My Drive\Spacesmith and Wordsmith's Tower\Spacesmith's HQ\Nuclear Energy and Propulsion Engineering\Accenture AI-ML Computational Scientist\GitHub\learning-log-ai-ml-2026\joining_data.ipynb"

with open(notebook_path, 'r', encoding='utf-8') as f:
    nb = json.load(f)

# Code to list tables
list_tables_code = [
    "# Get a list of available tables\n",
    "tables = list(client.list_tables(dataset))\n",
    "list_of_tables = [table.table_id for table in tables]\n",
    "\n",
    "# Print your answer\n",
    "print(list_of_tables)\n",
    "\n",
    "# Check your answer (uncomment if using Kaggle's automated check)\n",
    "# q_1.check()"
]

new_cell = {
    "cell_type": "code",
    "execution_count": None,
    "id": "list_tables_cell",
    "metadata": {},
    "outputs": [],
    "source": list_tables_code
}

# Insert after cell 2
nb['cells'].insert(3, new_cell)

with open(notebook_path, 'w', encoding='utf-8') as f:
    json.dump(nb, f, indent=1)

print("Notebook updated with list tables code.")

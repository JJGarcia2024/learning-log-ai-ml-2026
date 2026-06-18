import json

notebook_path = r"G:\My Drive\Spacesmith and Wordsmith's Tower\Spacesmith's HQ\Nuclear Energy and Propulsion Engineering\Accenture AI-ML Computational Scientist\GitHub\learning-log-ai-ml-2026\joining_data_exercise.ipynb"

with open(notebook_path, 'r', encoding='utf-8') as f:
    nb = json.load(f)

# The new code cell content with inline comments
code_snippet = [
    "# Your code here\n",
    "bigquery_experts_query = \"\"\"\n",
    "                         -- 5. Select the user ID (renaming it) and count their total rows\n",
    "                         SELECT a.owner_user_id AS user_id, COUNT(1) AS number_of_answers\n",
    "                         \n",
    "                         -- 1. Start with the questions table\n",
    "                         FROM `bigquery-public-data.stackoverflow.posts_questions` AS q\n",
    "                         \n",
    "                         -- 2. Join it with the answers table\n",
    "                         INNER JOIN `bigquery-public-data.stackoverflow.posts_answers` AS a\n",
    "                             ON q.id = a.parent_id\n",
    "                         \n",
    "                         -- 3. Filter for 'bigquery' tags\n",
    "                         WHERE q.tags LIKE '%bigquery%'\n",
    "                         \n",
    "                         -- 4. Group the rows by the user ID so each user only has one row\n",
    "                         GROUP BY a.owner_user_id\n",
    "                         \"\"\"\n",
    "\n",
    "# Set up the query\n",
    "safe_config = bigquery.QueryJobConfig(maximum_bytes_billed=10**10)\n",
    "bigquery_experts_query_job = client.query(bigquery_experts_query, job_config=safe_config)\n",
    "\n",
    "# API request - run the query, and return a pandas DataFrame\n",
    "# (Remember create_bqstorage_client=False to bypass the Storage API permission error)\n",
    "bigquery_experts_results = bigquery_experts_query_job.to_dataframe(create_bqstorage_client=False)\n",
    "\n",
    "# Preview results\n",
    "print(bigquery_experts_results.head())\n",
    "\n",
    "# Check your answer\n",
    "# q_5.check()"
]

new_cell = {
    "cell_type": "code",
    "execution_count": None,
    "id": "q5_exercise",
    "metadata": {},
    "outputs": [],
    "source": code_snippet
}

nb['cells'].append(new_cell)

with open(notebook_path, 'w', encoding='utf-8') as f:
    json.dump(nb, f, indent=1)

print("Notebook joining_data_exercise.ipynb appended with Q5 code.")

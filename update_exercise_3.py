import json

notebook_path = r"G:\My Drive\Spacesmith and Wordsmith's Tower\Spacesmith's HQ\Nuclear Energy and Propulsion Engineering\Accenture AI-ML Computational Scientist\GitHub\learning-log-ai-ml-2026\joining_data_exercise.ipynb"

with open(notebook_path, 'r', encoding='utf-8') as f:
    nb = json.load(f)

# The new code cell content
code_snippet = [
    "# Your code here\n",
    "questions_query = \"\"\"\n",
    "                  -- Select the specific columns requested\n",
    "                  SELECT id, title, owner_user_id\n",
    "                  FROM `bigquery-public-data.stackoverflow.posts_questions`\n",
    "                  -- Use LIKE with '%' wildcards to find any tag containing 'bigquery'\n",
    "                  WHERE tags LIKE '%bigquery%'\n",
    "                  \"\"\"\n",
    "\n",
    "# Set up the query (cancel the query if it would use too much of \n",
    "# your quota, with the limit set to 10 GB here, 10**10 bytes)\n",
    "safe_config = bigquery.QueryJobConfig(maximum_bytes_billed=10**10)\n",
    "\n",
    "# Send the query to the BigQuery client to start running it\n",
    "questions_query_job = client.query(questions_query, job_config=safe_config) \n",
    "\n",
    "# Wait for the query to finish and convert the results into a pandas DataFrame\n",
    "questions_results = questions_query_job.to_dataframe()\n",
    "\n",
    "# Preview the first five results\n",
    "print(questions_results.head())\n",
    "\n",
    "# Check your answer\n",
    "# q_3.check()"
]

new_cell = {
    "cell_type": "code",
    "execution_count": None,
    "id": "q3_exercise",
    "metadata": {},
    "outputs": [],
    "source": code_snippet
}

nb['cells'].append(new_cell)

with open(notebook_path, 'w', encoding='utf-8') as f:
    json.dump(nb, f, indent=1)

print("Notebook joining_data_exercise.ipynb appended with Q3 code.")

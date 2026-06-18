import json

notebook_path = r"G:\My Drive\Spacesmith and Wordsmith's Tower\Spacesmith's HQ\Nuclear Energy and Propulsion Engineering\Accenture AI-ML Computational Scientist\GitHub\learning-log-ai-ml-2026\joining_data_exercise.ipynb"

with open(notebook_path, 'r', encoding='utf-8') as f:
    nb = json.load(f)

# The new code cell content
code_snippet = [
    "# Your code here\n",
    "answers_query = \"\"\"\n",
    "                SELECT a.id, a.body, a.owner_user_id\n",
    "                FROM `bigquery-public-data.stackoverflow.posts_questions` AS q\n",
    "                INNER JOIN `bigquery-public-data.stackoverflow.posts_answers` AS a\n",
    "                    ON q.id = a.parent_id\n",
    "                WHERE q.tags LIKE '%bigquery%'\n",
    "                \"\"\"\n",
    "\n",
    "# Set up the query\n",
    "safe_config = bigquery.QueryJobConfig(maximum_bytes_billed=27*10**10)\n",
    "answers_query_job = client.query(answers_query, job_config=safe_config)\n",
    "\n",
    "# API request - run the query, and return a pandas DataFrame\n",
    "# (Remember to include create_bqstorage_client=False to prevent the 403 error!)\n",
    "answers_results = answers_query_job.to_dataframe(create_bqstorage_client=False)\n",
    "\n",
    "# Preview results\n",
    "print(answers_results.head())\n",
    "\n",
    "# Check your answer\n",
    "# q_4.check()"
]

new_cell = {
    "cell_type": "code",
    "execution_count": None,
    "id": "q4_exercise",
    "metadata": {},
    "outputs": [],
    "source": code_snippet
}

nb['cells'].append(new_cell)

with open(notebook_path, 'w', encoding='utf-8') as f:
    json.dump(nb, f, indent=1)

print("Notebook joining_data_exercise.ipynb appended with Q4 code.")

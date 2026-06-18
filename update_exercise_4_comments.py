import json

notebook_path = r"G:\My Drive\Spacesmith and Wordsmith's Tower\Spacesmith's HQ\Nuclear Energy and Propulsion Engineering\Accenture AI-ML Computational Scientist\GitHub\learning-log-ai-ml-2026\joining_data_exercise.ipynb"

with open(notebook_path, 'r', encoding='utf-8') as f:
    nb = json.load(f)

# The new code cell content with inline comments
code_snippet = [
    "# Your code here\n",
    "answers_query = \"\"\"\n",
    "                -- 5. Grab the specific columns requested from the answers table (using the 'a' alias)\n",
    "                SELECT a.id, a.body, a.owner_user_id\n",
    "                \n",
    "                -- 1. Start with the questions table and assign it the alias 'q'\n",
    "                FROM `bigquery-public-data.stackoverflow.posts_questions` AS q\n",
    "                \n",
    "                -- 2. Join it with the answers table, assigning it the alias 'a'\n",
    "                INNER JOIN `bigquery-public-data.stackoverflow.posts_answers` AS a\n",
    "                    \n",
    "                    -- 3. Link them where the question's 'id' matches the answer's 'parent_id'\n",
    "                    ON q.id = a.parent_id\n",
    "                \n",
    "                -- 4. Filter to only include rows where the question's tags contain 'bigquery'\n",
    "                WHERE q.tags LIKE '%bigquery%'\n",
    "                \"\"\"\n",
    "\n",
    "# Set up the query (cancel if it exceeds 270 GB)\n",
    "safe_config = bigquery.QueryJobConfig(maximum_bytes_billed=27*10**10)\n",
    "\n",
    "# Submit the query to BigQuery to start executing\n",
    "answers_query_job = client.query(answers_query, job_config=safe_config)\n",
    "\n",
    "# Wait for completion and download as pandas DataFrame \n",
    "# (Remember create_bqstorage_client=False to bypass the Storage API permission error)\n",
    "answers_results = answers_query_job.to_dataframe(create_bqstorage_client=False)\n",
    "\n",
    "# Preview the first 5 results\n",
    "print(answers_results.head())\n",
    "\n",
    "# Check your answer\n",
    "# q_4.check()"
]

# Find the Q4 cell we added earlier and replace its source
for cell in nb['cells']:
    if cell.get('id') == 'q4_exercise':
        cell['source'] = code_snippet
        break

with open(notebook_path, 'w', encoding='utf-8') as f:
    json.dump(nb, f, indent=1)

print("Notebook joining_data_exercise.ipynb updated with commented Q4 code.")

import json

notebook_path = r"G:\My Drive\Spacesmith and Wordsmith's Tower\Spacesmith's HQ\Nuclear Energy and Propulsion Engineering\Accenture AI-ML Computational Scientist\GitHub\learning-log-ai-ml-2026\joining_data_exercise.ipynb"

with open(notebook_path, 'r', encoding='utf-8') as f:
    nb = json.load(f)

# The new code cell content with the python function
code_snippet = [
    "# 6. Building a more generally useful service\n",
    "def expert_finder(topic, client):\n",
    "    '''\n",
    "    Returns a DataFrame with the user IDs who have written answers on a given topic.\n",
    "    '''\n",
    "    \n",
    "    # We use an f-string (notice the 'f' before the quotes) \n",
    "    # to dynamically inject our topic variable into the SQL query.\n",
    "    my_query = f\"\"\"\n",
    "               SELECT a.owner_user_id AS user_id, COUNT(1) AS number_of_answers\n",
    "               FROM `bigquery-public-data.stackoverflow.posts_questions` AS q\n",
    "               INNER JOIN `bigquery-public-data.stackoverflow.posts_answers` AS a\n",
    "                   ON q.id = a.parent_id\n",
    "               WHERE q.tags LIKE '%{topic}%'\n",
    "               GROUP BY a.owner_user_id\n",
    "               ORDER BY number_of_answers DESC\n",
    "               \"\"\"\n",
    "               \n",
    "    # Set up the query\n",
    "    safe_config = bigquery.QueryJobConfig(maximum_bytes_billed=10**10)\n",
    "    my_query_job = client.query(my_query, job_config=safe_config)\n",
    "    \n",
    "    # API request - run the query, and return a pandas DataFrame\n",
    "    results = my_query_job.to_dataframe(create_bqstorage_client=False)\n",
    "    \n",
    "    return results\n",
    "\n",
    "# Let's test it out by finding experts on 'python' instead of 'bigquery'!\n",
    "# python_experts = expert_finder('python', client)\n",
    "# print(python_experts.head())"
]

new_cell = {
    "cell_type": "code",
    "execution_count": None,
    "id": "q6_exercise",
    "metadata": {},
    "outputs": [],
    "source": code_snippet
}

nb['cells'].append(new_cell)

with open(notebook_path, 'w', encoding='utf-8') as f:
    json.dump(nb, f, indent=1)

print("Notebook joining_data_exercise.ipynb appended with Q6 code.")

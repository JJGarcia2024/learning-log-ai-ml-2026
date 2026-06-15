# ---
# jupyter:
#   jupytext:
#     formats: ipynb,py:percent
#     text_representation:
#       extension: .py
#       format_name: percent
#       format_version: '1.3'
#       jupytext_version: 1.19.3
#   kernelspec:
#     display_name: Study Env
#     language: python
#     name: study_env
# ---

# %%
import os
os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = (
    r"G:\My Drive\Spacesmith and Wordsmith's Tower\Spacesmith's HQ"
    r"\Nuclear Energy and Propulsion Engineering\Accenture AI-ML Computational Scientist"
    r"\Credentials\gen-lang-client-0137385761-b0d89e37e8e5.json"
)

# %%
from google.cloud import bigquery
from google.oauth2 import service_account

# Google Cloud credentials
credentials = service_account.Credentials.from_service_account_file(

    r"G:\My Drive\Spacesmith and Wordsmith's Tower\Spacesmith's HQ\Nuclear Energy and Propulsion Engineering\Accenture AI-ML Computational Scientist\Credentials\gen-lang-client-0137385761-b0d89e37e8e5.json"

)

# %%
from google.cloud import bigquery

# Create a "Client" object
client = bigquery.Client()

# Construct a reference to the "github_repos" dataset
dataset_ref = client.dataset("github_repos", project="bigquery-public-data")

# API request - fetch the dataset
dataset = client.get_dataset(dataset_ref)

# Construct a reference to the "licenses" table
licenses_ref = dataset_ref.table("licenses")

# API request - fetch the table
licenses_table = client.get_table(licenses_ref)

# Preview the first five lines of the "licenses" table
client.list_rows(licenses_table, max_results=5).to_dataframe()

# %%
# Construct a reference to the "sample_files" table
files_ref = dataset_ref.table("sample_files")

# API request - fetch the table
files_table = client.get_table(files_ref)

# Preview the first five lines of the "sample_files" table
client.list_rows(files_table, max_results=5).to_dataframe()

# %%
# =============================================================================
# 1. DEFINE THE SQL QUERY
# =============================================================================
# This query calculates how many files fall under each open-source license.
query = """
        -- 1a. Choose the columns to return: the license type, and the total count.
        -- COUNT(1) counts the number of rows (files) for each license group.
        SELECT L.license, COUNT(1) AS number_of_files
        
        -- 1b. The starting table is 'sample_files' (aliased as 'sf').
        FROM `bigquery-public-data.github_repos.sample_files` AS sf
        
        -- 1c. We merge (INNER JOIN) the 'sample_files' table with the 'licenses' table (aliased as 'L').
        -- We link them together where the 'repo_name' matches in both tables.
        -- This lets us look up the specific license for each file's repository.
        INNER JOIN `bigquery-public-data.github_repos.licenses` AS L 
            ON sf.repo_name = L.repo_name
            
        -- 1d. Grouping collapses all rows with the same license into a single row.
        -- This is required so our COUNT(1) function knows what to count.
        GROUP BY L.license
        
        -- 1e. Sort the final output so the license with the most files is at the top.
        ORDER BY number_of_files DESC
        """

# =============================================================================
# 2. CONFIGURE THE JOB SAFETY LIMITS
# =============================================================================
# BigQuery charges by data scanned. To avoid a massive unexpected bill if you 
# make a mistake, we set a hard limit (quota) of 10 Gigabytes (10**10 bytes).
# If the query needs to scan more than 10 GB, it will immediately cancel itself.
safe_config = bigquery.QueryJobConfig(maximum_bytes_billed=10**10)

# Submit the query to Google's servers to run, attaching our safety configuration.
query_job = client.query(query, job_config=safe_config)

# =============================================================================
# 3. FETCH THE RESULTS
# =============================================================================
# Wait for the query to finish, download the result data, and convert it into 
# a Pandas DataFrame so we can easily view and analyze it in Python.
# 
# (Note: create_bqstorage_client=False tells it to use the standard download method
# because your account doesn't have the permissions required for the faster Storage API).
file_count_by_license = query_job.to_dataframe(create_bqstorage_client=False)

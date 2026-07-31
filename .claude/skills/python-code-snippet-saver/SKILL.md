---
name: python-code-snippet-saver
description: >
  Activate for saving and organizing Python example code snippets, lessons, and study materials.
  Triggers on: "save this code," "store this snippet," "organize my Python examples," or any
  request related to saving and managing Python code snippets. Always activate BEFORE saving or
  organizing content. Covers Python fundamentals, advanced topics, libraries, frameworks, and
  best practices.
---

# Python Code Snippet Saver

When activated, save Python code snippets, examples, and study materials to the local repository
in a structured, retrievable format.

## Save Location

Save all Python code snippets as `.py` files in:

```
Lessons/Python Fundamentals/Code Snippets/
```

Create this directory if it does not exist.

## File Naming Convention

Name files descriptively using snake_case:

```
<topic>_<subtopic>.py
```

Examples:
- `variables_naming_conventions.py`
- `loops_list_comprehension.py`
- `pandas_groupby_examples.py`

## File Format

Every saved snippet file must include a header comment block:

```python
# Topic: <topic name>
# Subtopic: <subtopic or concept>
# Source: <where this came from, e.g., "Kaggle Learn", "MIT OCW", "practice session">
# Date: <YYYY-MM-DD>
# Description: <one-sentence summary of what this snippet demonstrates>

# --- Code ---
<snippet here>
```

## Process

1. Identify the topic and subtopic of the snippet.
2. Check if a file for this topic already exists in `Lessons/Python Fundamentals/Code Snippets/`.
   - If yes: append the new snippet to the existing file with a clear separator (`# ---`).
   - If no: create a new file with the naming convention above.
3. Confirm the save location and filename to the user after saving.

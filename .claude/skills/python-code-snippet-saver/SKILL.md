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
Python Fundamental Skills\Code Snippets\
```

Create this directory if it does not exist. (Note: this is NOT
`Lessons/Python Fundamentals/...` — that is a different, unrelated folder
in this repo.)

## File Naming Convention

Name files to match the topic, using snake_case, and matching the
corresponding lecture note's filename stem where one exists (e.g. `scope.py`
alongside `scope.md`):

```
<topic>.py
```

Examples already in this folder:
- `xargs.py`
- `types_of_functions.py`
- `scope.py`

## File Format

Every saved snippet file must include a header comment block matching the
existing style in that folder:

```python
# Topic: <topic name>
# Subtopic: <subtopic or concept>
# Source: <where this came from, e.g., "Complete Python Mastery (Mosh Hamedani)">
# Description: <one-sentence summary of what this snippet demonstrates>

# --- Code ---
<snippet here>
```

## Process

1. Identify the topic and subtopic of the snippet.
2. Check if a file for this topic already exists in `Python Fundamental Skills\Code Snippets\`.
   - If yes: append the new snippet to the existing file with a clear separator (`# ---`).
   - If no: create a new file with the naming convention above.
3. Confirm the save location and filename to the user after saving.

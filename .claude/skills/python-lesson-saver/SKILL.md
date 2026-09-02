---
name: python-lesson-saver
description: >
  Activate for saving and organizing Python lessons, notes, and study materials.
  Triggers on: "save this lesson," "store this note," "organize my Python notes," or any
  request related to saving and managing Python learning resources. Always activate BEFORE
  saving or organizing content. Covers Python fundamentals, advanced topics, libraries,
  frameworks, and best practices.
---

# Python Lesson Saver

When activated, save Python lessons, notes, and study materials to the local repository in a
structured, retrievable format.

## Save Location

Save all Python lesson notes as `.md` files in:

```
Python Fundamental Skills\Lecture Notes\
```

Create this directory if it does not exist. (Note: this is NOT
`Lessons/Python Fundamentals/...` — that is a different, unrelated folder
in this repo.)

## File Naming Convention

Name files to match the topic, matching the corresponding code snippet's
filename stem where one exists (e.g. `scope.md` alongside `scope.py`):

```
<topic>.md
```

Examples already in this folder:
- `xargs.md`
- `types_of_functions.md`
- `scope.md`

## File Format

Every saved lesson file must match the existing style in that folder — a
plain title and source line, not a YAML-style frontmatter block:

```markdown
# <Lesson Title>

**Source:** <course or resource name, e.g., "Complete Python Mastery (Mosh Hamedani, codewithmosh.com)">

<lesson content here>
```

## Process

1. Identify the lesson topic and source.
2. Check if a file for this lesson already exists in `Python Fundamental Skills\Lecture Notes\`.
   - If yes: merge/append the new material into it with a `---` separator instead of creating a near-duplicate file.
   - If no: create a new file with the naming convention above.
3. Confirm the save location and filename to the user after saving.

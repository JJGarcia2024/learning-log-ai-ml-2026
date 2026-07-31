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
Lessons/Python Fundamentals/Lecture Notes/
```

Create this directory if it does not exist.

## File Naming Convention

Name files descriptively using kebab-case:

```
<week-or-unit>-<topic>.md
```

Examples:
- `week1-variables-and-types.md`
- `unit3-pandas-dataframes.md`
- `intro-sql-groupby.md`

## File Format

Every saved lesson file must include a frontmatter block:

```markdown
# <Lesson Title>

**Topic:** <topic>
**Source:** <course or resource name, e.g., "Kaggle Learn SQL", "MIT OCW 6.009">
**Date:** <YYYY-MM-DD>

---

<lesson content here>
```

## Process

1. Identify the lesson topic, source, and date.
2. Check if a file for this lesson already exists in `Lessons/Python Fundamentals/Lecture Notes/`.
   - If yes: confirm with the user before overwriting or appending.
   - If no: create a new file with the naming convention above.
3. Confirm the save location and filename to the user after saving.

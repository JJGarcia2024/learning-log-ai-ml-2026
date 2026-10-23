---
name: stage-3-implementation-plan
description: Breaks a reviewed design into an ordered, atomic backlog of sub-tasks — module structure, data structures, library responsibilities, one sub-task in flight at a time. Use after stage-2-design-review and before stage-4-tests-first, for any software, data/ML, or simulation deliverable.
---

# Stage 3 — Implementation Plan

AI-assisted. The AI chunks a reviewed spec into an ordered backlog well; the
human approves the ordering and scope boundaries — this is the solo
equivalent of sprint planning.

## Workflow

1. Break the design into discrete, ordered sub-tasks — small enough that each
   one is independently checkable.
2. Name module/file structure, data structures, and library responsibilities
   up front, before writing any of them.
3. Flag where the likely performance or complexity bottleneck sits, even
   before Stage 5.
4. One sub-task in flight at a time — monotropic focus. Don't propose
   parallel work streams for a single human to context-switch across.
5. Get the ordering approved before Stage 4.

## Project-type notes

- [reference/project-types.md](reference/project-types.md)

## Gate to advance

An ordered sub-task list, approved, with module/data-structure/library
choices named and a bottleneck called out if one is expected.

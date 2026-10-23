---
name: stage-0-brainstorm
description: Scopes a new piece of work into one narrow, falsifiable goal before any planning, design, or code exists. Use at the very start of a new software, data/ML, or simulation deliverable (API, RAG app, agent workflow, full-stack UI, scientific code, exam prep), or whenever the user states a goal that is vague, open-ended, or unfalsifiable ("improve X", "build an app", "get better at Y").
---

# Stage 0 — Brainstorm

Manual only. Understanding what's actually worth building is judgment, not
pattern-matching — the AI can list options, the human picks. Do not let this
stage get skipped or folded into Stage 1 (`stage-1-plan-spec`) — a vague goal
fed into planning produces a vague plan at higher speed, not a better one.

## What "done" looks like

One sentence, falsifiable, with a pass/fail condition:

- Bad: "improve CFD skills" · "build an app" · "get better at AI/ML"
- Good: "Can a PINN reproduce RAE2822 pressure within X%?" · "Ship a LlamaIndex
  RAG repo with Ragas metrics documented and a DeepEval CI gate green"

## Workflow

1. Ask what the human is actually trying to prove, ship, or learn — the
   falsifiable claim, not the activity.
2. If the stated goal is vague, propose 2–3 narrowed candidates and ask which
   one. Don't pick for them — that's the one judgment call this stage exists
   to protect.
3. Confirm where this goal lands in the human's plan (tracker, board, or
   notes file) before scoping further — see the project-type notes below.
4. Stop here. Do not draft requirements yet — that's Stage 1
   (`stage-1-plan-spec`).

## Project-type notes

- [reference/project-types.md](reference/project-types.md)

## Gate to advance

One narrow, falsifiable goal statement, agreed by the human, mapped to a
specific slot in the human's plan (sprint, milestone, or deadline).

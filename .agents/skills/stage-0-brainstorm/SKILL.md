---
name: stage-0-brainstorm
description: Scopes a new piece of software work into one narrow, falsifiable goal before any planning, design, or code exists. Use at the start of any software project (web app, mobile app, API, AI/ML model, CLI/library, data pipeline, simulation) — including AI Systems Engineering Roadmap (Hardened Edition) deliverables and aerospace portfolio projects — or whenever the user states a goal that is vague, open-ended, or unfalsifiable ("improve X", "build an app", "get better at Y").
---

# Stage 0 — Brainstorm

Manual only. Understanding what's actually worth building is judgment, not
pattern-matching — Claude can list options, the human picks. Do not let this
stage get skipped or folded into Stage 1 (`stage-1-plan-spec`) — a vague goal
fed into planning produces a vague plan at higher speed, not a better one.

## What "done" looks like

One sentence, falsifiable, with a pass/fail condition:

- Bad: "improve CFD skills" · "build an app" · "get better at AI/ML"
- Good: "Can a PINN reproduce RAE2822 pressure within X%?" · "Ship a LlamaIndex
  RAG repo with Ragas metrics documented and a DeepEval CI gate green" · "On
  a real Android phone, a user can create a habit offline and it survives an
  app restart"

## Workflow

1. Ask what the human is actually trying to prove, ship, or learn — the
   falsifiable claim, not the activity.
2. If the stated goal is vague, propose 2–3 narrowed candidates and ask which
   one. Don't pick for them — that's the one judgment call this stage exists
   to protect.
3. Pick the track — general, plus a roadmap overlay if the work is on a
   board — and if there is a board, read its current state before scoping
   further. See the track files below.
4. Stop here. Do not draft requirements yet — that's Stage 1
   (`stage-1-plan-spec`).

## Track-specific scoping

Read the **general track** first — it applies to any software. If the work
maps to a roadmap board, also read that board's overlay; where they
disagree, the overlay wins (it carries the board's own definition of done).

- **General track** (web, mobile, API, AI/ML model, CLI/library, data
  pipeline): [reference/general-track.md](reference/general-track.md)
- **AI/ML overlay** — AI Systems Engineering Roadmap: Hardened Edition:
  [reference/aiml-track.md](reference/aiml-track.md)
- **Aerospace overlay** — AI/ML × Aerospace Engineering Master Roadmap
  (2026–2036):
  [reference/aerospace-track.md](reference/aerospace-track.md)

## Gate to advance

One narrow, falsifiable goal statement, agreed by the human, with a deadline
or time budget — and, if the work is on a roadmap board, mapped to a
specific slot on it.

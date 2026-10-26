---
name: stage-4-tests-first
description: Defines what "correct" means and writes the validation check before the feature code — the acceptance test that decides "done," not a claim of it. Use after stage-3-implementation-plan and before stage-5-build, for any software project (web app, mobile app, API, AI/ML model, CLI/library, data pipeline, simulation) — unit/integration/end-to-end tests, eval metrics, benchmarks, analytic limits.
---

# Stage 4 — Tests First

Manual target, AI-assisted scaffolding. The human chooses the validation
target that decides "done"; Claude writes the assertion around it. This is
TDD discipline applied to the whole project, not just unit tests: the check
gets written before the feature it's checking.

## Workflow

1. State, in writing, what "correct" looks like numerically or behaviorally
   — before any feature code exists.
2. Write the check: a unit, integration, or end-to-end test, a benchmark
   comparison, an eval-metric threshold, an analytic-limit test, a
   conservation-law check, or a CI gate.
3. Confirm the check currently fails (there's no feature yet to pass it) —
   a check that already passes against nothing is testing the wrong thing.
4. Only then move to Stage 5 (`stage-5-build`).

## Track-specific validation targets

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

A written, currently-failing check that defines "done" for this sub-task.

---
name: stage-4-tests-first
description: Defines what "correct" means and writes the validation check before the feature code — the acceptance test that decides "done," not a claim of it. Use after stage-3-implementation-plan and before stage-5-build, for either an AI/ML deliverable (pytest, Ragas/DeepEval, type checks) or an aerospace simulation project (analytic limits, benchmarks, conservation laws).
---

# Stage 4 — Tests First

Manual target, AI-assisted scaffolding. The human chooses the validation
target that decides "done"; Claude writes the assertion around it. This is
TDD discipline applied to the whole board, not just unit tests: the check
gets written before the feature it's checking.

## Workflow

1. State, in writing, what "correct" looks like numerically or behaviorally
   — before any feature code exists.
2. Write the check: an assertion, a benchmark comparison, an analytic-limit
   test, a conservation-law check, or a CI eval gate.
3. Confirm the check currently fails (there's no feature yet to pass it) —
   a check that already passes against nothing is testing the wrong thing.
4. Only then move to Stage 5 (`stage-5-build`).

## Track-specific validation targets

- **AI/ML track**: [reference/aiml-track.md](reference/aiml-track.md)
- **Aerospace track**: [reference/aerospace-track.md](reference/aerospace-track.md)

## Gate to advance

A written, currently-failing check that defines "done" for this sub-task.

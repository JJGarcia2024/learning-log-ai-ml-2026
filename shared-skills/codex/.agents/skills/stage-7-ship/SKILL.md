---
name: stage-7-ship
description: Staging-to-production gate — convergence/sensitivity/CI checks, then the actual commit or submission. Use after stage-6-second-review passes, for any software, data/ML, or simulation deliverable (repo goes public, CI green, tracker item marked Done).
---

# Stage 7 — Ship

Delegate. CI/CD-style pipelines run ~95% automated; human intervention is
reserved for actual incidents, not for re-litigating a decision Stage 6
already made. This stage is mechanical once Stage 6 passed — don't reopen
design questions here.

## Workflow

1. Run the staging-equivalent checks for this project type (see below) — these are
   the last automated gate before the commit/submission is real.
2. Make the commit / submission / status update.
3. Update the relevant board (sprint tracker, project board, or notes) — a shipped
   item that isn't marked Done on the board doesn't count toward the gate.
4. Stage ∞ (`stage-infinite-debug-maintain`) starts immediately after —
   shipping isn't the end of the loop.

## Project-type notes

- [reference/project-types.md](reference/project-types.md)

## Gate to advance

The commit/submission is made, CI (where applicable) is green, and the board
row is updated to Done/Shipped/Passed.

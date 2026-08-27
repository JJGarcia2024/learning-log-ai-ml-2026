---
name: stage-7-ship
description: Staging-to-production gate — convergence/sensitivity/CI checks, then the actual commit or submission. Use after stage-6-second-review passes, for either an AI/ML deliverable (repo goes public, CI badge green, gate item marked Done) or an aerospace simulation project (portfolio commit with a full RESULT block).
---

# Stage 7 — Ship

Delegate. CI/CD-style pipelines run ~95% automated; human intervention is
reserved for actual incidents, not for re-litigating a decision Stage 6
already made. This stage is mechanical once Stage 6 passed — don't reopen
design questions here.

## Workflow

1. Run the staging-equivalent checks for this track (see below) — these are
   the last automated gate before the commit/submission is real.
2. Make the commit / submission / status update.
3. Update the relevant board (sprint tracker or Timeline board) — a shipped
   item that isn't marked Done on the board doesn't count toward the gate.
4. Stage ∞ (`stage-infinite-debug-maintain`) starts immediately after —
   shipping isn't the end of the loop.

## Track-specific shipping checklist

- **AI/ML track**: [reference/aiml-track.md](reference/aiml-track.md)
- **Aerospace track**: [reference/aerospace-track.md](reference/aerospace-track.md)

## Gate to advance

The commit/submission is made, CI (where applicable) is green, and the board
row is updated to Done/Shipped/Passed.

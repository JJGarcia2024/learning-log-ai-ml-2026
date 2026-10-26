---
name: stage-7-ship
description: Staging-to-production gate — final checks, then the actual merge, release, deploy, or submission, plus the board or changelog update. Use after stage-6-second-review passes, for any software project (web app, mobile app, API, AI/ML model, CLI/library, data pipeline, simulation) — a deployed app, released library, published model, or roadmap/portfolio deliverable.
---

# Stage 7 — Ship

Delegate. CI/CD-style pipelines run ~95% automated; human intervention is
reserved for actual incidents, not for re-litigating a decision Stage 6
already made. This stage is mechanical once Stage 6 passed — don't reopen
design questions here.

## Workflow

1. Run the staging-equivalent checks for this track (see below) — these are
   the last automated gate before the commit/submission is real.
2. Make the merge, release, deploy, or submission.
3. If the work is on a roadmap board, update its row — a shipped item that
   isn't marked Done on the board doesn't count toward the gate. If there's
   no board, record it in the project's changelog or README.
4. Stage ∞ (`stage-infinite-debug-maintain`) starts immediately after —
   shipping isn't the end of the loop.

## Track-specific shipping checklist

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

The merge/release/submission is made, CI (where applicable) is green, and
the board row — or the project's changelog, if there's no board — is updated.

# Stage 6 on the aerospace track

Defer to `wordsmith-ntp-sim-coder`'s skeptical code-review phase. Does the
Stage 4 test actually pass? Does a comment claim a physical justification the
code doesn't implement? Is a simpler fix hiding a deeper bug?

## What this file adds

- The re-run output shown here must be the same numbers that go in the
  RESULT block's VALIDATION field and the repo README at Stage 7. Don't let
  them drift.
- Re-run from a clean checkout with the recorded seed. A threshold met only
  on one lucky seed is not met.
- For rungs validated against experiment (L7, L9), confirm the comparison
  uses the published dataset itself, not values read off a plot by eye,
  unless the README says so.

## Gate to advance

The code-review phase was run in full, and the three checks above were
re-run live.

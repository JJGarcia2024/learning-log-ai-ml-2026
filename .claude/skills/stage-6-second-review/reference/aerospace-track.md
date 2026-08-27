# Stage 6 on the aerospace track

Defer to `nep-coder` Phase 6 — Skeptical Code Review. It already implements
this stage for NTP/NEP work: does the Phase 4 test actually pass, does a
comment claim a physical justification the code doesn't implement, is there
a simpler fix hiding a deeper bug.

## What this file adds

- The re-run output nep-coder's Phase 6 demands should be the same output
  quoted in the eventual portfolio commit's `## RESULT` VALIDATION field
  (Stage 7) — don't let the two drift into different numbers.
- If this project sits inside a restored full-scope block on the Timeline
  board, confirm the review didn't quietly accept essentials-survey-depth
  rigor instead of the full-block rigor that block was restored to have.

## Gate to advance

Run nep-coder's Phase 6 in full; this file only adds the two checks above.

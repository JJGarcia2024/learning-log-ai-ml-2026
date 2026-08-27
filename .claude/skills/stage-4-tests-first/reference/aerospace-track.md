# Stage 4 on the aerospace track

Defer to `nep-coder` Phase 4 — Tests First, and its Section 5.1 Physics
Validation Hierarchy (units → analytic limit → conservation → grid
convergence → solver convergence → parameter sensitivity → cross-code
comparison). That hierarchy *is* this stage's content for NTP/NEP work.

## What this file adds

- For portfolio items (P1–P17), the check should match the project's stated
  validation target on `nep-coder` (e.g. Project 1: linear steady-state
  match; Project 7: NASA CEA agreement within ±3%) — don't write a looser
  check just because the board's hour budget is tight.
- "Done" as a claim is banned per nep-coder's non-negotiable rules — the
  Stage 4 check's actual output must be shown at Stage 6, not asserted.

## Gate to advance

Run nep-coder's Phase 4 in full; this file only confirms the target matches
the project's documented validation benchmark.

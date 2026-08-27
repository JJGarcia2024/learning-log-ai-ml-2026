# Stage ∞ on the aerospace track

Defer to `nep-coder` Section 5 — Debugging Protocol: the Physics Validation
Hierarchy (units → analytic limit → conservation → grid convergence → solver
convergence → parameter sensitivity → cross-code comparison), the per-tool
bug tables (Python CFD, OpenFOAM, SU2, OpenMC, WarpX, pyKEP/pygmo,
Cantera/CoolProp), and Section 5.3's five-step debugging session format
(Reproduce → Hypothesize → Isolate → Fix → Harden) — this stage's own
six-step workflow is that same format with an explicit human-decides step
added on top.

## What this file adds

- **Timeline risk as a "bug."** The board's own "Honest Risks" section
  (GKS language-year trap, Fall-2033 thesis-defense squeeze, motivation
  horizon, age/recency drift) are maintenance items on the same footing as a
  code bug — they get the same reproduce/hypothesize/isolate/fix/harden
  treatment, just applied to schedule risk instead of a stack trace.
- Re-verify funder age caps and rules ~18 months out (~mid-2031 per the
  board) as a standing maintenance check, same category as MLA-C02's
  availability check on the AI/ML track.

## Gate to advance

Run nep-coder's Section 5 debugging protocol in full; this file only extends
"harden" to cover schedule/funding risk, not only code.

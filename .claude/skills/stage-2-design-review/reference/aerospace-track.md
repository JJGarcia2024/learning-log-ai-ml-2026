# Stage 2 on the aerospace track

Defer to `nep-coder` Phase 2 — Skeptical Design Review. It already runs this
stage in full for NTP/NEP work: wrong governing equation, wrong numerical
method for the regime, missed edge case, wrong library choice (e.g. icoFoam
vs rhoCentralFoam), a simpler approach that was skipped.

## What this file adds

- If the project is a restored full-scope block (Compressible Flow, Solid
  Mechanics, Heat Transfer, Dynamics & Vibrations, Classical Control) on
  `Board_for_2026-2035_Extended_Study_Timeline`, check the design isn't
  quietly compressed back down to "essentials" survey depth — that
  compression was the exact risk the 2033/2035 timeline extension was built
  to remove.
- If the project sits near the Apr–Jun 2032 MEXT/GKS/KAIST application
  window on the board, flag schedule risk explicitly in the review, not just
  technical risk.

## Gate to advance

Run nep-coder's Phase 2 in full; this file only adds the two board-specific
checks above.

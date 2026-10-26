# Stage 5 on the aerospace track

Defer to `wordsmith-ntp-sim-coder`'s build phase: production/optimization
decisions named explicitly, and comments that give the quantity and the
physical reason, never a syntax description. Write the RESULT block after
every code block. Use that skill's comment standard directly. Don't redefine
one here.

## What this file adds

- Seed every stochastic run (PINN/FNO/GNN training) and record the seed in
  the RESULT block. Thresholds like "within 5%" aren't checkable if the run
  can't be reproduced.
- Name the units in every variable or docstring that carries a physical
  quantity.

## Gate to advance

The build phase was run in full, including its RESULT block.

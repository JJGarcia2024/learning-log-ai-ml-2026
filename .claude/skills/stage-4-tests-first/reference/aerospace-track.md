# Stage 4 on the aerospace track

Defer to `wordsmith-ntp-sim-coder`'s tests-first phase and its physics
validation hierarchy: units → analytic limit → conservation → grid
convergence → solver convergence → parameter sensitivity → cross-code or
experimental comparison.

## What this file adds: copy the board's number

When the month's Done Criteria states a threshold, that is the Stage 4 target,
verbatim. Examples from the Master Roadmap:

| Item | Board's own check |
|---|---|
| P2 Barba Steps 1–4 | Verified against analytical solutions |
| L6 Boris pusher | Cycloid trajectory, <0.1% energy drift |
| L7 Hall-thruster PINN | Thrust within 5% of experiment |
| L8 FNO | Generalizes across 15 operating points |
| L9 2D HallPINN | Divergence angle within 4% of experiment |
| L10 GNN surrogate | <10 ms on unseen geometries; sensitivity matches finite difference; 50x speedup table |

Don't write a looser check because the hour budget is tight.

## Gate to advance

The tests-first phase was run in full, and the check matches or is stricter
than the month's Done Criteria.

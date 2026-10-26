# Stage 7 on the aerospace track

Defer to `wordsmith-ntp-sim-coder`'s staging phase. Run the later validation
steps (grid/mesh convergence, solver convergence, parameter sensitivity,
cross-code or experimental comparison) before the commit, and use its commit
convention. Write the RESULT block only after staging passes.

## What this file adds: update the Master Roadmap

After the commit, update the **AI/ML × Aerospace Engineering Master Roadmap**
(`Monthly Timeline` tab):

- Set the month row's **Status** cell (e.g. "Planned" → "Done").
- "Shipped" for a P# or L# means what its Done Criteria says, e.g. "Ship P1
  Numerics Toolkit", "Ship completed L9 2D HallPINN repository": a public
  repo with the RESULT block's numbers in the README.

Read the sheet before writing. Don't overwrite cells you haven't read.

## Gate to advance

The staging phase and commit convention were run in full, and the month row's
Status reflects the shipped state.

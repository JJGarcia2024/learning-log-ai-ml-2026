# Stage 3 on the aerospace track

Defer to `wordsmith-ntp-sim-coder`'s implementation-plan phase: algorithm
choice, module structure, data structures, library responsibilities,
bottlenecks (e.g. O(N²) memory, Jacobi vs CG).

## What this file adds (Master Roadmap checks)

- **Fit the month.** The aerospace lane is 22 h/mo from Oct 2027, shared with
  that month's coursework. If the backlog doesn't fit, split it across the
  board's own months. P2 already spans Apr–Jun 2029 (Steps 1–4, 5–8, 9–12),
  and L7 spans Oct 2029 – Mar 2030.
- **Reuse, don't rebuild.** Later projects should import earlier toolkits:
  P1 (root-finding, quadrature, RK4) and P3 (LU, Cholesky, CG). Plan them as
  dependencies, not copies.
- **Packaging order.** Each portfolio project ends shipped as its own repo,
  per the Done Criteria ("Ship P# …"). Repo setup is a sub-task, not an
  afterthought.

## Gate to advance

The implementation-plan phase was run in full, and the backlog fits the
month row's hours, or the split across months has been approved.

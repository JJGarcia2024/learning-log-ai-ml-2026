# Stage 0 on the aerospace track

Source board: **AI/ML × Aerospace Engineering Master Roadmap (2026–2036)** —
Google Sheet `1KEmDsEdIwATpUnguiVGXI3rxZcKlv5KPUiTJ2T48VhU`, tab
`Monthly Timeline`. Columns: Month/Year • Phase & Track • AI/ML Career Focus •
Math & Aerospace Study Focus • **Monthly Milestone & Done Criteria** • Est.
Hours • Weekly Rhythm • Status.

Targets: AI/ML job-ready H2 2027 (Oct 2027 wave) • **Technion M.Sc. start
March 2034** (application deadline 1 Nov 2033) • **US PhD start 1 Sep 2036**.
Pre-MSc budget 1,740 h (~230 h margin): 46 h/mo, 160 h/mo once the MSc starts.

## Precedence with the Hardened roadmap (split by lane)

- **Oct 2026 – Sep 2027:** the AI/ML and AWS lanes follow the Hardened
  Edition roadmap (`aiml-track.md`). The Math/Physics lane (Anton Calculus I,
  MIT 8.01) follows this board.
- **Oct 2027 onward:** this board governs everything.

## Phases

| Phase | Months | Aerospace-side content |
|---|---|---|
| 1 Foundation & Job Readiness | Oct 2026 – Sep 2027 | Calculus I, Physics I (Mechanics) |
| 2 Industry Experience & Multivariable Math | Oct 2027 – Dec 2028 | Calc II–III, Sci-ML L3–L5, P1 Numerics Toolkit |
| 3 Classical Physics, LinAlg & Thermal-Fluids | Jan 2029 – Dec 2030 | E&M, 18.06, 18.03, Thermo, Fluids, P2–P4, Sci-ML L6–L8 |
| 4 Continuum Mech, Plasma & Research | Jan 2031 – Dec 2032 | PDEs, Compressible, Heat, Solids, Dynamics, Controls, Plasma for EP, Orbital, P5–P10, Sci-ML L9–L10, arXiv preprint |
| 5 GRE, Apps & Technion Transition | Jan 2033 – Feb 2034 | GRE, P11, Technion dossier, KAIST backup (Aug 2033) |
| 6 Technion MSc & US PhD Launch | Mar 2034 – Sep 2036 | Thesis, conference/journal papers, US PhD applications |

## Where a new goal has to land

A buildable goal maps to one of these:

- **Portfolio project P1–P11:** P1 Numerics Toolkit • P2 Barba 12 Steps •
  P3 Verified Solvers • P4 OpenFOAM/SU2 pipeline • P5 MOC Nozzle Contour
  Generator • P6 2D FEA • P7 6-DOF Flight Dynamics • P8 Satellite Attitude
  Control • P9 Astrodynamics & Low-Thrust Transfer • P10 RocketCEA • P11 Closed
  Brayton Cycle.
- **Sci-ML Ladder rung L3–L10:** inverse heat PINN → radiator fin → 1D nozzle
  → Boris pusher → 1D Hall-thruster PINN → FNO → 2D HallPINN → GNN surrogate.

Each also maps to the month row whose Done Criteria names it.

**Gate exams** (closed-notes, ≥80%) and coursework months are study work,
not software. Use `socratic-tutor` or `nep-roadmap-mentor` for those, not
Stages 0–7.

## Falsifiable-goal examples for this track

- Bad: "improve CFD skills"
- Good: "L6 Boris pusher reproduces the analytic cycloid trajectory with <0.1%
  energy drift" (Aug 2029's Done Criteria, verbatim)
- Good: "L7 1D Hall-thruster PINN predicts experimental thrust within 5%"
  (Feb 2030)

## Handoff after this stage

Once the goal is scoped, hand Stages 1–7 to `wordsmith-ntp-sim-coder`. Its
phases carry the simulation-specific detail: governing equations, physics
validation hierarchy, why-comments, RESULT block. Each later stage's
aerospace overlay points to the matching phase.

## Check before advancing

The goal names a P# or L# and the month row on the Master Roadmap whose Done
Criteria it fulfils. If either is missing, the goal isn't placed yet.

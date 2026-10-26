# Stage 2 on the aerospace track

Defer to `wordsmith-ntp-sim-coder`'s skeptical design-review phase: wrong
governing equation, wrong numerical method for the regime, missed edge case,
wrong solver/library (e.g. icoFoam vs rhoCentralFoam), simpler approach
skipped.

## What this file adds (Master Roadmap checks)

- **Prerequisite gate passed?** Does the design need math from a gate exam
  that hasn't been passed yet? For example, L7 Hall-thruster work assumes E&M,
  passed Jun 2029; P2 assumes Calc III, passed Dec 2028. If the gate isn't
  passed, the review flags it.
- **Validation against what?** Sci-ML rungs from L7 on validate against
  experimental data (published thruster data, PEPL/HPEPL). Is that dataset
  identified and actually obtainable?
- **Schedule risk near hard dates.** Work in Jun 2032 – Nov 2033 overlaps the
  preprint, PI outreach, GRE, and the 1 Nov 2033 Technion deadline. Flag
  schedule risk explicitly, not only technical risk.

## Gate to advance

The design-review phase was run in full, and the three checks above were asked
out loud.

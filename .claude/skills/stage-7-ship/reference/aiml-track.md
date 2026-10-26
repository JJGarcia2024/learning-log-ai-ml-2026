# Stage 7 on the AI/ML track

Shipping checklist by month row (**AI Systems Engineering Roadmap: Hardened
Edition**) — "shipped" means every DoD bullet on that row is ticked:

| Month | "Shipped" means |
|---|---|
| M1 | Typed API module merged to main • PyTorch loop + loss curve in repo • ≥2 AIF-C01 practice tests ≥80% • backprop README |
| M2 | CPU image <2 GB • CI green on every push • ClearML run logged • **AIF-C01 passed** • p50/p95 by hand = NumPy |
| M3 | **Repo 1**: p95 <15 ms, faithfulness >0.85 in CI, benchmark table + "Where this fails" |
| M4–M5 | Verified Burgers solver & spectral layer • trained FNO/DeepONet <5% L2 + PySR terms |
| M6 | **Repo 2**: p95 ≥40% below FastAPI baseline, 100% fallback on out-of-range inputs |
| M7–M8 | Quantized 8B model profile • vLLM benchmark suite + ≥1.5x speculative speed-up |
| M9 | **SAA-C03 passed** (CERT-1) • Ray Serve pipeline demo |
| M10 | **Repo 3**: 0 failed transitions in 100 runs, caps enforced, state diagram |
| M11–M12 | Cross-repo benchmarks (≥5 runs) • 30-company list • 5 mock interviews • 2 Looms • 15 outreach emails |

## Updating the board

After shipping, update the sheet (`Monthly Timeline` tab):
- The month row's **STATUS** column (e.g. "In Progress" → "Done")
- The header **CURRICULUM PROGRESS** counter ("N / 12 Mos Done") and the
  YEAR 1 TOTAL row's "N / 12 Done"

Read the sheet before writing; don't overwrite cells you haven't read.

## Target-date rule

Each month row has a target date (last day of the month). Ship what's ready on
that date. A missed DoD bullet is flagged, not hidden — and if ≥2 DoD boxes are
missed in 2 consecutive months, the board's own rule triggers the 15 h/mo
low-energy pace (see `stage-infinite-debug-maintain`). The Section 8.1 gate
date (2027-09-30) does not move.

## Gate to advance

The board reflects the shipped state, not just the repo/cert itself.

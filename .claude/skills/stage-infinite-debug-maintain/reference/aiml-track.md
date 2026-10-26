# Stage ∞ on the AI/ML track

Common failure classes by month row (**AI Systems Engineering Roadmap:
Hardened Edition**) — hypothesize from these first:

| Month | Likely root causes |
|---|---|
| M1 Python & Autograd | Pydantic lax coercion accepting input the contract meant to reject (Python vs JSON column of the conversion table); handler order letting a default 422 body through; gradients not zeroed between steps |
| M2 Containers & CI | Works locally, fails in CI (missing env var, different Python version); image bloated by build tools copied into the final stage |
| M3 Repo 1 RAG | Chunk boundary splitting facts across chunks; faithfulness drop after a corpus update that wasn't re-embedded; p95 regression from query embedding cost |
| M4–M5 Scientific ML | FFT normalization mismatch in the spectral layer; train/test leakage making the out-of-distribution set look easy |
| M6 Repo 2 Serving | ONNX export dropping dynamic axes; Triton batching config not matching the benchmark's batch sizes |
| M7–M8 LLM serving | Out-of-memory at high concurrency (KV cache sizing); speed-up vanishing because the draft model's acceptance rate is low |
| M10 Repo 3 Agent | State not matching the tool's Pydantic contract after a change; agent looping because a tool doesn't signal a terminal state |

## Maintenance beyond bugs

- **Low-energy pace rule (from the board):** if ≥2 DoD boxes are missed in 2
  consecutive months, switch to the 15 h/mo low-energy pace. That decision runs
  through this stage's human-decides step.
- Use the protected Buffer rows (one blank catch-up week per quarter, ~18 h)
  to absorb slips — buffer is empty by default, not pre-filled with new scope.
- Cut scope inside a month rather than letting the 25 h/mo home ceiling be
  exceeded or the Section 8.1 gate date (2027-09-30) move.

## Gate to advance

Root cause named with reasoning (not just "fixed it"), and a regression
check added.

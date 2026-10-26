# Stage 4 on the AI/ML track

Validation target by month row (**AI Systems Engineering Roadmap: Hardened
Edition**, "Definition of Done" column):

| Month | Write this check first |
|---|---|
| M1 Python & Autograd | pytest table of accepted/rejected inputs + exact error-shape assertions for the typed API; a check that the loss curve file exists and loss decreases |
| M2 Containers & CI | Image-size assertion (<2 GB CPU); CI workflow that fails on a deliberately broken test; p50/p95 by hand vs NumPy equality test |
| M3 Repo 1 RAG | DeepEval faithfulness >0.85 assertion in CI + p95 retrieval <15 ms benchmark + NumPy-vs-Qdrant ranking match on 10 queries — written before retrieval is built |
| M4 Operators & Solvers | Unit tests for spectral layer & DeepONet on toy inputs; Burgers solver vs analytic benchmark tolerance |
| M5 Surrogate & PySR | Relative L2 <5% on the test set; PySR coefficient tolerance 5% |
| M6 Repo 2 Serving | p95 ≥40% lower than FastAPI baseline at batch 1/8/32; 100% of out-of-range inputs hit the fallback |
| M7 Quantization | Perplexity increase ≤0.3 + task accuracy; single-command benchmark script |
| M8 vLLM | TTFT p50/p95 at 1/8/32 concurrency; speculative speed-up ≥1.5x at 1 user |
| M9 Ray | ≥100 parallel solver runs, measured speed-up vs Amdahl's prediction |
| M10 Repo 3 Agent | 0 failed state transitions across 100 runs; caps + fallback path tested |
| M11 Benchmarking | Mean ± spread over ≥5 runs for every reported metric |
| M12 Pitch | Each Section 8.1 gate criterion as an individual pass/fail check, not one vague "done" |

## Standing rule

If the month's DoD already states the check (e.g. M3's "DeepEval faithfulness
>0.85 validated in GitHub Actions CI"), copy it verbatim as the Stage 4
target — don't invent a softer one.

## Gate to advance

The check matches (or is stricter than) the month's own DoD bullet.

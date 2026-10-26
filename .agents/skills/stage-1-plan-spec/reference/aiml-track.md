# Stage 1 on the AI/ML track

What "plan/spec" means per month row on the **AI Systems Engineering Roadmap:
Hardened Edition (2026–2027)** sheet (`Monthly Timeline` tab):

| Month | Spec should pin down |
|---|---|
| M1 Production Python & Autograd | Typed API module shape (request/response models, strict fields, validators, exception handlers); what the PyTorch loop trains and where the loss curve is saved |
| M2 Containers, CI/CD, MLOps | Docker base image + what goes in each build stage (CPU image <2 GB); which tests CI runs on push; which metrics/artifacts ClearML logs |
| M3 Repo 1 Zotero RAG | Corpus size (≥5,000 chunks), the two chunking strategies compared, Qdrant config, local model (Ollama/llama.cpp), DeepEval faithfulness gate in CI |
| M4 Operator Learning & Solvers | Spectral layer + DeepONet interfaces, Burgers analytic benchmark chosen, Darcy dataset layout in HDF5 (≥2 initial-condition families) |
| M5 Surrogate Training & PySR | Train/test/out-of-distribution split definition, relative L2 metric, which PDE terms PySR must rediscover |
| M6 Repo 2 Serve-a-Surrogate | ONNX export path, Triton `config.pbtxt` (dynamic batching), named GPU, FastAPI baseline, input-range guard + solver fallback rule |
| M7 Quantization & KV Cache | Which 8B model, AWQ settings, task-accuracy benchmark, GPU rental budget cap |
| M8 vLLM & Speculative Decoding | Named GPU, prompt length (512 tokens), concurrency levels (1/8/32), draft model / EAGLE-3 choice |
| M9 Ray | Solver sweep size (≥100 runs), Ray Serve pipeline stages, scaling policy |
| M10 Repo 3 Simulation Agent | Typed state machine states/transitions, Pydantic contracts, step & token caps, fallback path, 100-run benchmark |
| M11 Benchmarking | Metric set per repo (TTFT/TPOT; p50/p95/p99, throughput, relative L2), N ≥5 runs |
| M12 Pitch & Outreach | Loom scripts (3 min), outreach list and tracking pipeline |

## Spec template

```
Goal (from Stage 0): ...
Roadmap slot: M# • <month> — DoD bullet: "<verbatim>" — home hrs budget: ... — target date: ...
Inputs: ...
Outputs / deliverable: ...
Validation target (formalized in Stage 4): ...
Explicitly out of scope: ...   (name work that belongs to a later month's DoD, e.g. CI is M2)
```

## Check before advancing

Does the spec name a concrete validation target that matches the month's own
DoD bullet on the sheet? A spec whose "done" doesn't match the DoD will pass
Stage 1 and fail the month's target date.

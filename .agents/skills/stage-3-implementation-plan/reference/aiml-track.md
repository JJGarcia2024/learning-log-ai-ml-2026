# Stage 3 on the AI/ML track

Typical sub-task ordering per month row (**AI Systems Engineering Roadmap:
Hardened Edition**):

- **M1 Typed API module**: Pydantic request/response models → custom
  exception classes → exception handlers → route → tests → merge to main.
  Write the Stage-4 assertions before the route logic (see
  `stage-4-tests-first`).
- **M1 PyTorch loop**: data + model → manual forward/loss/backward/step →
  loss logging → saved loss-curve plot.
- **M2 Containers & CI**: Dockerfile (multi-stage) → image size check →
  GitHub Actions test job → ClearML logging.
- **M3 Repo 1 RAG**: ingestion/chunking (both strategies) → embeddings →
  Qdrant → retrieval + latency benchmark → DeepEval harness → CI gate →
  NumPy cosine cross-check → README (benchmark table, "Where this fails").
  The eval harness is a sub-task, not an afterthought.
- **M4–M5 Surrogates**: solver + analytic check → dataset (HDF5) → layers +
  unit tests → training → out-of-distribution eval → PySR.
- **M6 Repo 2 Serving**: ONNX export → Triton config → FastAPI baseline →
  input-range guard + solver fallback → benchmark (batch 1/8/32) → README.
- **M10 Repo 3 Agent**: typed state + Pydantic contracts → individual tools →
  state machine transitions → caps + fallback → 100-run benchmark → state
  diagram. Contracts first because everything downstream depends on their shape.

## Bottleneck flags specific to this track

- M3 RAG: embedding time at ≥5,000 chunks on CPU — size the corpus before
  committing to a model.
- M6–M8: rented GPU time — every run must stay inside the pre-allocated cloud
  budget; batch experiments so the GPU isn't idle while you debug.
- Any month: 25 h/mo home ceiling — if the backlog exceeds the row's home
  hours, cut scope inside the month, don't borrow from the next.

## Gate to advance

Sub-task order matches the dependency chain above (or a justified deviation
from it), and one sub-task is marked "in flight" — not several.

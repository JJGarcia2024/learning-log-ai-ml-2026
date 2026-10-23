# Stage 3 — typical sub-task ordering by project type

- **API service**: Pydantic models → route handlers → docs config → tests →
  packaging. Write the Stage-4 assertions before the route logic (see
  `stage-4-tests-first`).
- **RAG / LLM app**: ingestion/chunking → embeddings → vector store →
  retrieval → hybrid search → eval harness → CI gate → README + diagram. The
  eval harness is a sub-task, not an afterthought — order it before polish.
- **Agent workflow**: tool schemas → individual tool functions → graph
  topology → end-to-end wiring → docs/RAG layer last. Schemas first, because
  everything downstream depends on their shape.
- **Full-stack UI**: backend streaming endpoint → minimal UI shell → wire the
  streaming UI → deploy → write-ups last, once the repos are shippable.
- **Simulation / scientific code**: governing equations → discretization →
  solver → boundary conditions → validation case → parameter study.

## Bottleneck flags

- RAG: embedding/vector-store cost and latency at corpus scale — size the
  corpus before committing to a vector DB.
- Agent: state-passing overhead when the tool has many parameters — keep tool
  I/O schemas flat.
- Simulation: memory and complexity (e.g. O(N²) storage, direct vs iterative
  solver) — name it before building.

## Gate to advance

Sub-task order matches the dependency chain (or a justified deviation), and
exactly one sub-task is marked "in flight".

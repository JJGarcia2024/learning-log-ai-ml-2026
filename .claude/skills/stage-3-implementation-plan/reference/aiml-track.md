# Stage 3 on the AI/ML track

Typical sub-task ordering per sprint deliverable:

- **S2 FastAPI**: Pydantic models → route handlers → Swagger config → tests
  → packaging. Don't write tests before the models exist to validate against
  — but do write the Stage-4 assertions before the route logic (see
  `stage-4-tests-first`).
- **S4 RAG flagship**: ingestion/chunking → embeddings → vector store →
  retrieval → hybrid search → Ragas eval harness → DeepEval CI gate → README
  + diagram. The eval harness is a sub-task, not an afterthought — order it
  before "grounded cited answers" polish work.
- **S5 Agentic agent**: tool schemas (Pydantic) → individual tool functions
  → LangGraph graph topology → end-to-end wiring → RAG-over-docs layer last.
  Tool schemas first because everything downstream depends on their shape
  being right.
- **S6 Full-stack**: backend streaming endpoint → minimal Next.js shell →
  wire streaming UI → SAA-C03 booking (parallel, not blocking) → resume/app
  drafts last, once repos are actually shippable.

## Bottleneck flags specific to this track

- RAG (S4): embedding/vector-store cost and latency at corpus scale — size
  the corpus before committing to a vector DB choice.
- Agent (S5): LangGraph state-passing overhead if the sim has many
  parameters — keep the Pydantic tool I/O schema flat where possible.

## Gate to advance

Sub-task order matches the dependency chain above (or a justified deviation
from it), and one sub-task is marked "in flight" — not several.

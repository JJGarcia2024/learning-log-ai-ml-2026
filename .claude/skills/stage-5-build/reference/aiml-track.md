# Stage 5 on the AI/ML track

Conventions by sprint deliverable:

- **S1 Python**: full type hints (as the Pydantic on-ramp, per the tracker's
  own framing), docstrings, explicit error handling — no bare `except:`.
- **S2 FastAPI**: Pydantic models for every request/response, Swagger docs
  generated from the models (not hand-maintained), tests alongside routes.
- **S4 RAG flagship**: cite chunking/embedding choices in comments (why this
  chunk size for this corpus), keep the Ragas/DeepEval harness runnable as
  its own script, not buried inside notebook cells.
- **S5 Agentic agent**: pin LangGraph and dependency versions explicitly (the
  tracker calls this out — "create_agent, pinned versions"); Pydantic tool
  I/O schemas should reject malformed sim parameters, not pass them through.
- **S6 Full-stack**: streaming implemented end-to-end (backend to UI), not
  faked with a spinner; keep the Next.js shell minimal — this is a proof of
  full-stack capability, not a product.

## Git hygiene (Essential #8, applies to every sprint)

Clean commit history, README-first repos, one green CI pipeline — this is
itself one of the 8 Essentials, not a nice-to-have. A repo without it doesn't
count as shipped for the sprint gate.

## Gate to advance

Code matches the sprint's own convention above and the repo's CI (where
applicable) is green, not just "should pass."

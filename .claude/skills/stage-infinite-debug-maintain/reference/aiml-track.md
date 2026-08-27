# Stage ∞ on the AI/ML track

Common failure classes by sprint deliverable — hypothesize from these first:

| Sprint | Likely root causes |
|---|---|
| S1 Python | Type hints present but not enforced by a checker; silent `except:` swallowing the real error |
| S2 FastAPI | Pydantic model out of sync with the actual route logic; Swagger docs stale relative to the models |
| S4 RAG flagship | Chunking boundary splitting facts across chunks (retrieval finds neither half); Ragas score drop after a corpus update that wasn't re-embedded |
| S5 Agentic agent | LangGraph state not matching the tool's Pydantic schema after a sim-parameter change; agent looping because a tool doesn't signal terminal state |
| S6 Full-stack | Streaming works locally, breaks in deployment (buffering/proxy issue); env var present locally, missing in the deployed target |

## Maintenance beyond bugs

- Re-verify MLA-C02 exam availability (~Apr 2027 per the tracker's own
  fragile-cert note) — this is a scheduled maintenance check, not a bug, but
  belongs in this stage because it's ongoing monitoring, not a one-time gate.
- If a sprint slips, cut scope inside the sprint — per the tracker's own
  Lever 2 — rather than letting the Dec 31 wall move. That scope-cut decision
  runs through this stage's human-decides step.

## Gate to advance

Root cause named with reasoning (not just "fixed it"), and a regression
check added.

# Stage 4 on the AI/ML track

Validation target by sprint deliverable:

| Sprint | Write this check first |
|---|---|
| S1 Python | pytest assertions for the typed module's functions/classes, run before the module is "rebuilt from memory" for the gate |
| S2 FastAPI | Test hitting `/predict`, asserting the Pydantic response shape and a 200 — before route logic is filled in |
| S3 AWS AIF-C01 | Practice-exam score threshold that counts as "passing level" — pick the number before grinding practice exams |
| S4 RAG flagship | The DeepEval pytest CI assertion + target Ragas thresholds (faithfulness, context precision) — written before retrieval is built, not measured after and rationalized |
| S5 Agentic agent | The end-to-end scenario script: a specific natural-language request and the exact sim output that counts as success — written before the LangGraph graph exists |
| S6 Full-stack | Each of the 7 Dec 31 Gate checklist items, treated as individual pass/fail checks, not one vague "done" |

## Standing rule

If a sprint's own gate on `Wordsmith_Essentials_Sprint_Dec31` already states
the check (e.g. S4's "DeepEval CI green + Ragas metrics documented"), copy it
verbatim as the Stage 4 target — don't invent a softer one.

## Gate to advance

The check matches (or is stricter than) the sprint's own documented gate.

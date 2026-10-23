# Stage 4 — validation target by project type

| Project type | Write this check first |
|---|---|
| Python module | pytest assertions for the module's functions/classes, run before the module is built |
| API service | A test hitting the endpoint, asserting the response model's shape and a 200 — before the route logic is filled in |
| RAG / LLM app | The eval pytest/CI assertion plus target metric thresholds (faithfulness, context precision) — written before retrieval is built, not measured after and rationalized |
| Agent workflow | An end-to-end scenario script: a specific natural-language request and the exact output that counts as success — written before the graph exists |
| Full-stack UI | Each checklist item treated as an individual pass/fail check, not one vague "done" |
| Simulation / scientific code | Walk the validation hierarchy: units → analytic limit → conservation → grid convergence → solver convergence → parameter sensitivity → cross-code comparison |
| Exam / certification | The practice-exam score that counts as passing — pick the number before grinding practice exams |

## Standing rule

If the plan slot already states its own check (e.g. "eval CI green + metrics
documented"), copy it verbatim as the Stage 4 target — don't invent a softer
one.

## Gate to advance

The check matches (or is stricter than) the slot's own documented gate.

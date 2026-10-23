# Stage 1 — what the spec should pin down

| Project type | Spec should pin down |
|---|---|
| Python module | Which typed module (functions, classes, typing, error handling, I/O); what "written without reference" will be verified against |
| API service | Endpoint shape, Pydantic models, what the generated docs must show, test coverage target |
| RAG / LLM app | Corpus, chunking/embedding/vector-store choice, search scope, which eval metrics and CI gate will decide "done" |
| Agent workflow | Which tool or simulation gets wrapped, graph framework + tool I/O schema shape, what "runs end-to-end from a natural-language request" means concretely |
| Full-stack UI | UI scope (streaming?), which backend it is wired to, which checklist items this deliverable covers |
| Simulation / scientific code | Governing equations, inputs/outputs, units, validation target (analytic limit or published benchmark) |
| Exam / certification | Exam blueprint scope, booking date, practice-exam pass threshold |

## Spec template

```
Goal (from Stage 0): ...
Plan slot: ... — target hrs: ...
Inputs: ...
Outputs / deliverable: ...
Validation target (formalized in Stage 4): ...
Explicitly out of scope: ...
```

## Check before advancing

Does the spec name a concrete validation target that matches the plan slot's
own definition of done? A spec whose "done" doesn't match the slot's gate will
pass Stage 1 and fail the deadline.

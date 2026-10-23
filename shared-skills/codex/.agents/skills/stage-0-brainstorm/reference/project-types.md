# Stage 0 — placing and scoping a goal

Every goal should trace to something bigger than itself: a job target, a
course deadline, a portfolio slot. Ask the human where this goal lands in
their plan (a tracker, project board, or notes file — whatever they use) and
how many hours it gets. If it doesn't map to a slot, it isn't scoped yet: go
back to the human, don't invent a slot for them.

## Falsifiable-goal examples by project type

| Project type | Bad | Good |
|---|---|---|
| Python module | "get better at Python" | "A typed module with pytest coverage, clean under `mypy --strict`, that I can rebuild from memory" |
| API service | "learn FastAPI" | "A FastAPI service whose `/predict` returns a validated Pydantic response, Swagger docs generated, tests green in CI" |
| RAG / LLM app | "get good at RAG" | "A RAG repo over corpus X with Ragas faithfulness + context precision scored and an eval CI gate green" |
| Agent workflow | "build an agent" | "An agent that runs tool X end-to-end from a natural-language request, with Pydantic-validated tool I/O" |
| Full-stack UI | "build an app" | "A chat UI streaming live from the backend, deployed, and click-through verified" |
| Simulation / scientific code | "improve CFD skills" | "Does 1D FTCS heat conduction reproduce the analytic linear steady state within numerical tolerance?" |
| Exam / certification | "study for the cert" | "Pass exam X by date Y, after first hitting Z% on practice exams" |

## Check before advancing

Does the goal have a target-hour count and a slot in the human's plan? If
not, it isn't scoped yet.

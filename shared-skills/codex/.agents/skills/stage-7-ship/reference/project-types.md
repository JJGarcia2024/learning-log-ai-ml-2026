# Stage 7 — what "shipped" means by project type

| Project type | "Shipped" means |
|---|---|
| Python module | Typed module + passing tests in a public repo |
| API service | Working service in a public repo, with generated docs |
| RAG / LLM app | Eval CI green + metrics documented in the README; repo public |
| Agent workflow | Agent runs end-to-end from a natural-language request; repo public |
| Full-stack UI | Chat UI streaming live from the backend; every checklist item Done |
| Simulation / scientific code | Convergence and sensitivity checks pass; commit message or README quotes the validation result |
| Exam / certification | Status set to "Passed" (not "Scheduled") with the certificate |

## Updating your tracker

After shipping, update whatever you use to track progress (sprint board,
issue tracker, spreadsheet, notes): the item's actual hours and status. A
shipped item that isn't marked Done doesn't count toward the gate.

## Deadline rule

When a hard deadline arrives, ship what is ready and flag the slipped part as
in-progress rather than moving the deadline. Scope can slip inside a sprint;
the wall doesn't move.

## Gate to advance

The tracker reflects the shipped state, not just the repo or certificate
itself.

# Stage 5 on the AI/ML track

Conventions by month row (**AI Systems Engineering Roadmap: Hardened Edition**):

- **M1 Python & Autograd**: full type hints; Pydantic v2 APIs only
  (`field_validator`, `model_validator`, `ConfigDict`); strict fields where
  the contract needs them; explicit error handling — no bare `except:`;
  training loop written without high-level wrappers.
- **M2 Containers & CI**: multi-stage Dockerfile, pinned base image; CI runs
  the same test command used locally.
- **M3 / M6 / M10 Flagship repos**: README-first; benchmark table and a
  "Where this fails" section in every README; cite design choices in comments
  (why this chunk size, why this batch size, why this state).
- **M4–M5 Scientific ML**: seed every run; store datasets in HDF5 with the
  generating parameters as attributes.
- **M6–M8 GPU work**: name the GPU in every benchmark; keep benchmark scripts
  single-command and committed.
- **M10 Agent**: pin dependency versions; Pydantic contracts reject malformed
  tool inputs, not pass them through; LLM only plans/selects tools.

## Git hygiene (applies to every month)

Clean commit history, README-first repos, and — from M2 on — CI green on every
push. A repo without it doesn't count as a flagship for the Section 8.1 gate.

## Gate to advance

Code matches the month's convention above and the repo's CI (from M2 on) is
green, not just "should pass."

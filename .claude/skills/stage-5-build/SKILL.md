---
name: stage-5-build
description: The actual implementation, once a spec, design review, ordered backlog, and failing test all already exist — feature code with line-by-line comments explaining why, not what. Use only after stage-4-tests-first, for either an AI/ML deliverable (FastAPI, RAG, LangGraph, Next.js code) or an aerospace simulation project (solvers, configs, physics code).
---

# Stage 5 — Build

Delegate, with review. This is the stage where the ratio flips hardest —
roughly 80% Claude-generated code from an approved spec, 20% human
review/edge cases. That ratio only holds because Stages 1–4 already happened;
building without them is vibe-coding, not delegation.

## Preconditions (check before writing code)

- [ ] Stage 1 spec exists and was signed off
- [ ] Stage 2 design review happened and objections were resolved
- [ ] Stage 3 sub-task order is approved, one sub-task in flight
- [ ] Stage 4's check exists and currently fails

If any box is unchecked, stop and go back — don't build around the gap.

## Workflow

1. Implement the current sub-task only — not the whole backlog at once.
2. Comment every non-trivial line at the "why," not the "what": name the
   reason (physical law, business rule, performance tradeoff), not a
   restatement of the syntax.
3. Run the Stage 4 check as you go, not only at the end.
4. Stop at the sub-task boundary and hand off to Stage 6
   (`stage-6-second-review`) — don't self-review in the same pass that wrote
   the code.

## Track-specific build conventions

- **AI/ML track**: [reference/aiml-track.md](reference/aiml-track.md)
- **Aerospace track**: [reference/aerospace-track.md](reference/aerospace-track.md)

## Gate to advance

The current sub-task's code exists, is commented at the "why" level, and the
Stage 4 check has actually been run against it (pass or fail shown, not
assumed).

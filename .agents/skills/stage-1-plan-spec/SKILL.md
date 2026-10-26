---
name: stage-1-plan-spec
description: Pins down requirements and constraints before any code exists — Claude drafts, the human signs off. Use once a goal has been scoped in stage-0-brainstorm, for any software project (web app, mobile app, API, AI/ML model, CLI/library, data pipeline, simulation), including Hardened roadmap and aerospace work, before any implementation planning or build work begins.
---

# Stage 1 — Plan / Spec

Manual, AI drafts. Claude can draft requirements and flag gaps, but unclear
intent fed into Claude becomes unclear output at higher speed — human sign-off
is required before implementation starts. Do not proceed to Stage 2
(`stage-2-design-review`) on a draft the human hasn't actually read.

## What this stage produces

A written spec, before any architecture decision:
- The falsifiable goal from Stage 0, restated
- Inputs, outputs, and constraints
- The validation target that will decide "done" (this gets formalized in
  Stage 4, but name it now)
- Anything explicitly out of scope

## Workflow

1. Draft the spec from the Stage 0 goal — don't ask the human to write it
   from scratch, draft it and let them correct it.
2. Flag every place the goal was ambiguous rather than silently resolving it
   your own way.
3. Get explicit sign-off before moving on. "Looks right, go ahead" counts;
   silence doesn't.
4. Pull the track-specific spec template — see below.

## Track-specific spec content

Read the **general track** first — it applies to any software. If the work
maps to a roadmap board, also read that board's overlay; where they
disagree, the overlay wins (it carries the board's own definition of done).

- **General track** (web, mobile, API, AI/ML model, CLI/library, data
  pipeline): [reference/general-track.md](reference/general-track.md)
- **AI/ML overlay** — AI Systems Engineering Roadmap: Hardened Edition:
  [reference/aiml-track.md](reference/aiml-track.md)
- **Aerospace overlay** — AI/ML × Aerospace Engineering Master Roadmap
  (2026–2036):
  [reference/aerospace-track.md](reference/aerospace-track.md)

## Gate to advance

A spec the human has explicitly signed off on, naming inputs, outputs,
constraints, and the intended validation target.

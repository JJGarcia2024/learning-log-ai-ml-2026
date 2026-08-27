---
name: stage-1-plan-spec
description: Pins down requirements and constraints before any code exists — Claude drafts, the human signs off. Use once a goal has been scoped in stage-0-brainstorm, for either an AI/ML upskilling deliverable (FastAPI, RAG, LangGraph, AWS cert, Next.js) or an aerospace portfolio project (CFD, OpenFOAM, SU2, OpenMC, BRIDGE-30), before any implementation planning or build work begins.
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

- **AI/ML track**: [reference/aiml-track.md](reference/aiml-track.md)
- **Aerospace track**: [reference/aerospace-track.md](reference/aerospace-track.md)

## Gate to advance

A spec the human has explicitly signed off on, naming inputs, outputs,
constraints, and the intended validation target.

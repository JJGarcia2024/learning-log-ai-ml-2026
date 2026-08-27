---
name: stage-2-design-review
description: Attacks a signed-off spec before any build starts, from a skeptical-reviewer persona — wrong architecture, wrong pattern, missed edge case, simpler approach skipped. Use after stage-1-plan-spec and before stage-3-implementation-plan, for either an AI/ML deliverable (FastAPI, RAG, LangGraph, Next.js) or an aerospace simulation project (CFD, OpenFOAM, SU2, OpenMC, BRIDGE-30).
---

# Stage 2 — Design Review

Mostly manual, AI assists. Architecture trade-offs still need a human who
understands the trade-off; Claude's real contribution here is diagrams and
flagging known pitfalls, not making the call. Do this even for small work —
a spec that survives unchallenged usually means the review didn't happen, not
that the spec was flawless.

## Workflow

1. Switch personas: read the Stage 1 spec as a skeptical senior reviewer, not
   as its author.
2. Attack specifically — not "looks fine," but named failure modes: wrong
   pattern for this stack/regime, missed edge case, a simpler approach that
   was skipped, a library/solver choice that doesn't fit.
3. The human either defends the design or the design changes. No design
   proceeds to Stage 3 unchallenged.
4. Track-specific attack checklists below.

## Track-specific review checklists

- **AI/ML track**: [reference/aiml-track.md](reference/aiml-track.md)
- **Aerospace track**: [reference/aerospace-track.md](reference/aerospace-track.md)

## Gate to advance

The spec has been attacked on at least the track-specific checklist items,
and each attack was either answered or the spec was revised.

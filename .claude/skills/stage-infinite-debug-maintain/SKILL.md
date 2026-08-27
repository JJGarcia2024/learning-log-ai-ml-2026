---
name: stage-infinite-debug-maintain
description: Runs underneath every other stage, not after them — reproduce, isolate, root-cause, fix, prevent recurrence. Use whenever something breaks, drifts, or needs upkeep on an already-shipped AI/ML deliverable (FastAPI, RAG, LangGraph, Next.js) or aerospace simulation project (CFD, OpenFOAM, SU2, OpenMC, BRIDGE-30) — not only after stage-7-ship, but at any point a bug surfaces during any other stage.
---

# Stage ∞ — Debug & Maintain

Split: Claude monitors and drafts, human decides whether to apply. Debugging
isn't "a phase" that runs after Stage 7 — it runs the whole way through,
underneath every stage above. Treat any bug found mid-Stage-5 as entering
this stage immediately, not as a note to fix "later."

## Workflow (do not skip steps)

1. **Reproduce.** Full error/traceback + minimal reproducible case. State
   what the failure *means* — physically, or for the deliverable — before
   touching anything.
2. **Hypothesize.** Two most likely root causes, each with actual reasoning,
   not just guesses.
3. **Isolate.** The smallest test that distinguishes between the hypotheses.
4. **Fix.** Explain (a) why the original was wrong, (b) why the fix is
   correct, (c) what the fix doesn't address.
5. **Harden.** Add an assertion or test capturing the invariant that was
   violated, so this class of bug can't silently return.
6. Human decides whether to apply the fix — that "decide whether" clause is
   the whole point of this stage's split ownership.

## Track-specific debugging references

- **AI/ML track**: [reference/aiml-track.md](reference/aiml-track.md)
- **Aerospace track**: [reference/aerospace-track.md](reference/aerospace-track.md)

## Gate to advance

The fix is applied (human-approved), and a new assertion/test now guards the
specific invariant that broke.

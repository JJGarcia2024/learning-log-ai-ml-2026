---
name: stage-6-second-review
description: An independent skeptical pass over Stage 5's output — not the same context that wrote the code congratulating itself. Checks whether the Stage 4 test actually passes (output shown, not asserted), whether a comment claims a justification the code doesn't implement, and whether the fix hides a deeper bug. Use after stage-5-build and before stage-7-ship, for either an AI/ML deliverable or an aerospace simulation project.
---

# Stage 6 — Second Review

AI-assisted first pass, MANUAL final call. Claude can assist every phase but
doesn't own accountability or the final "ship it" decision when being wrong
has a real cost. "Done" is a claim, not proof — this stage exists to demand
the Stage 4 check's actual output, not a restatement that it "should work."

## Workflow

1. Re-read Stage 5's output from a skeptical persona — a second, independent
   pass, genuinely distinct from the pass that wrote it.
2. Re-run the Stage 4 check and show its actual output. A claim of "it works"
   without the test's real output is rejected outright — re-run and show it.
3. Check for comments that claim a physical/business justification the code
   doesn't actually implement.
4. Check for a simpler fix that might be hiding a deeper bug.
5. Human makes the final ship/no-ship call — Claude's pass informs it, never
   replaces it.

## Track-specific review checklists

- **AI/ML track**: [reference/aiml-track.md](reference/aiml-track.md)
- **Aerospace track**: [reference/aerospace-track.md](reference/aerospace-track.md)

## Gate to advance

The Stage 4 check's actual output has been shown (not asserted), and the
human has made the final call.

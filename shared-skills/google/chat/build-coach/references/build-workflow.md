# Build workflow: 8 stages plus Debug

Used by the coach. One stage at a time. Each stage ends with a **gate**: a
concrete thing that must be true before moving on. The human confirms the gate.

**Why stages?** Vague goals fed into an AI produce vague code faster. Each
stage removes a different kind of vagueness before any code exists.

## Stage 0 — Brainstorm
Turn a fuzzy idea into ONE falsifiable sentence with a pass/fail condition.
- Bad: "build an app", "get better at AI/ML".
- Good: "Ship a FastAPI service whose /predict returns a validated response,
  with tests green in CI."
Coach: ask what the human wants to prove, ship, or learn. If the goal is vague,
offer 2–3 narrower options and let the human pick. Don't pick for them.
Also ask where this fits in their plan and how many hours it gets.
**Gate:** one narrow, falsifiable goal the human agreed to.

## Stage 1 — Spec
The coach drafts, the human reads and corrects. Contents: the goal restated;
inputs, outputs, constraints; the validation target that decides "done";
what is out of scope. Flag every ambiguity instead of silently resolving it.
**Gate:** the human explicitly says the spec is right ("looks right, go
ahead"; silence doesn't count).

## Stage 2 — Design review
The coach plays a skeptical senior reviewer and attacks the spec with NAMED
failure modes: wrong pattern for this stack, missed edge case, a simpler
approach that was skipped, a library that doesn't fit. "Looks fine" is not
allowed. The human defends the design or changes it.
**Gate:** every attack was answered or the spec was revised.

## Stage 3 — Plan
Break the design into small, ordered sub-tasks, each independently checkable.
Name files, data structures and library roles up front. Flag the likely
bottleneck. Only ONE sub-task in flight at a time.
**Gate:** the human approved the order.

## Stage 4 — Tests first
Write down what "correct" means, then write the check BEFORE the feature.
Confirm it currently FAILS. A check that passes against nothing is testing the
wrong thing. If the plan already states the check, copy it; don't soften it.
**Gate:** a written check that currently fails.

## Stage 5 — Build
Preconditions: spec signed off, design reviewed, plan approved, failing test
exists. If any is missing, go back. Build ONE sub-task. Comment the WHY on
non-trivial lines. Ask the human to run the test and paste the output.
**Gate:** code exists and the test result (pass or fail) was pasted, not
assumed.

## Stage 6 — Second review
A fresh, skeptical re-read, as if someone else wrote the code. Ask the human to
re-run the test and paste the real output. Check: does any comment claim
something the code doesn't do? Is a simple fix hiding a deeper bug? The human
makes the final ship/no-ship call.
**Gate:** real test output shown, and the human decided.

## Stage 7 — Ship
Final checks (tests green, no secrets in the commit, README says what it is),
then commit/publish, then update whatever the human uses to track progress. A
shipped item not marked done doesn't count. At a hard deadline: ship what is
ready and flag what slipped, don't move the deadline.
**Gate:** committed or submitted, and the tracker is updated.

## Stage ∞ — Debug and maintain (any time)
Whenever something breaks, in any stage:
1. **Reproduce:** full error message plus the smallest example. Say what the
   failure means before changing anything.
2. **Hypothesize:** two likely causes, with reasons.
3. **Isolate:** the smallest test that tells them apart.
4. **Fix:** why the original was wrong, why the fix is right, what it does
   NOT address.
5. **Harden:** add a test for the thing that broke.
The human decides whether to apply the fix.
**Gate:** root cause named, plus a new test guarding it.

## Project-type cheat sheet

| Project type | "Done" check to write first (Stage 4) | Common bug |
|---|---|---|
| Python module | pytest assertions; type checker clean | Type hints not enforced; bare `except:` hides errors |
| API service | Test the endpoint's response shape and status 200 | Response model out of sync with the route |
| RAG / LLM app | Eval metrics with target thresholds, in CI | Chunk boundaries split facts; corpus changed but not re-embedded |
| Agent workflow | End-to-end scenario with exact expected output | Agent loops because a tool never signals "done" |
| Full-stack UI | Each checklist item is its own pass/fail | Works locally, breaks deployed (env vars, buffering) |
| Simulation / science code | Units, analytic limit, conservation, convergence | Units mismatch; solver not converged |
| Exam / certification | Practice-exam score that counts as passing | "Scheduled" mistaken for "Passed" |

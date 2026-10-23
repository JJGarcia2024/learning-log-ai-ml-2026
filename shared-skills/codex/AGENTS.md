# Working agreement

This repo ships a set of skills in `.agents/skills/`. Use them.

## How we build things

Work in this order and say which stage you are in at the start of each reply:
`stage-0-brainstorm` → `stage-1-plan-spec` → `stage-2-design-review` →
`stage-3-implementation-plan` → `stage-4-tests-first` → `stage-5-build` →
`stage-6-second-review` → `stage-7-ship`. `stage-infinite-debug-maintain`
applies at any time something breaks.

- Never skip a stage. Each stage ends with a "gate"; ask me to confirm it.
- One sub-task in flight at a time.
- Write the failing test/check before the feature code.
- Never say something "works" without running it and showing the real output.
- Comment code with the reason (why), not a restatement of the syntax.

## Safety

- Never commit secrets (`.env`, API keys, tokens). Check `git diff --staged`
  before every commit.
- Ask before destructive Git operations (force push, hard reset, deleting
  branches).

## Helpers

`coder-skill` (Python help), `devops-git` (Git/GitHub/CI),
`python-lesson-saver` and `python-code-snippet-saver` (keep study notes).

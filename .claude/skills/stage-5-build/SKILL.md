---
name: stage-5-build
description: The actual implementation, once a spec, design review, ordered backlog, and failing test all already exist — the human writes the feature code, and Claude acts as a suggester (like a formatter's diff the human accepts or rejects), never editing the files. Comments explain why, not what. Use only after stage-4-tests-first, for any software project (web app, mobile app, API, AI/ML model, CLI/library, data pipeline, simulation).
---

# Stage 5 — Build

The human builds, and Claude suggests. Think of Black's `--diff`: Claude
proposes a change, and the human accepts, rejects, or edits it. Claude
replaces the hours spent searching Stack Overflow and docs, not the thinking.
That split is what keeps this from being vibe-coding. Stages 1–4 already
decided *what* to build, so each suggestion only has to answer *how*.

## Suggester rules

- **The human owns the files.** Claude never uses Edit/Write on feature or
  test code during this stage. Suggestions go in the chat only.
- **Answer the question asked.** Fit the suggestion to the human's current
  code, as a small before/after or diff, with one line on *why*.
- **One sub-task at a time.** Never hand over the whole module in one reply.
  A suggestion bigger than the current sub-task is vibe-coding in disguise.
- **The human may paste accepted code** — typing by hand isn't required.
  The check is understanding: if they can't explain a line, they ask "why?"
  before accepting it.
- **Don't predict that tests pass.** The human runs the Stage 4 check, and
  Claude reads the real output.

## Preconditions (check before writing code)

- [ ] Stage 1 spec exists and was signed off
- [ ] Stage 2 design review happened and objections were resolved
- [ ] Stage 3 sub-task order is approved, one sub-task in flight
- [ ] Stage 4's check exists and currently fails

If any box is unchecked, stop and go back — don't build around the gap.

## Workflow

1. The human implements the current sub-task only — not the whole backlog
   at once — asking Claude for syntax and suggestions as they go.
2. Comment every non-trivial line at the "why," not the "what": name the
   reason (physical law, business rule, performance tradeoff), not a
   restatement of the syntax. Claude's suggestions carry the same comments.
3. Run the Stage 4 check as you go, not only at the end.
4. Stop at the sub-task boundary and hand off to Stage 6
   (`stage-6-second-review`). Its findings also come back as suggestions
   for the human to apply.

## Track-specific build conventions

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

The current sub-task's code exists, the human wrote or accepted every line
and can explain it, it is commented at the "why" level, and the Stage 4 check
has actually been run against it (pass or fail shown, not assumed).

# Stage 0 on the general track

The base track for any software: web app, mobile app, backend/API, AI/ML
model, CLI or library, data pipeline, game, script. Use it on its own for work
that isn't on a roadmap board, or under a roadmap overlay
(`aiml-track.md`, `aerospace-track.md`) for work that is.

## Questions that narrow any goal

1. **Who uses it, and for what one job?** Name a user and a task, not a
   feature list.
2. **What's the smallest version that proves the idea?** One screen, one
   endpoint, one model, one command.
3. **What would make it fail?** If nothing could, the goal isn't falsifiable.
4. **What's the deadline or time budget?** A goal without one expands to fill
   all available time.
5. **Is this to ship, to learn, or to prove?** Each gives a different "done."

## Falsifiable-goal examples by project type

| Type | Bad | Good |
|---|---|---|
| Web app | "build a budgeting site" | "A user can sign up, add 3 expenses, and see a monthly total on a deployed URL; Lighthouse accessibility ≥90" |
| Mobile app | "make a habit app" | "On a physical Android phone, a user can create a habit and check it off offline; state survives an app restart" |
| Backend/API | "make an API" | "`POST /orders` rejects 8 named invalid payloads with 422 and a single error shape; p95 <100 ms locally" |
| AI/ML model | "train a better classifier" | "Beat the logistic-regression baseline by ≥5 F1 points on a held-out test set that's never used for tuning" |
| CLI / library | "write a tool" | "`tool convert in.csv` produces valid JSON for the 5 fixture files; installs with `pip install .`" |
| Data pipeline | "clean the data" | "Nightly job loads yesterday's rows with 0 duplicates and fails loudly on schema drift" |

## Check before advancing

The goal names a user or consumer, one pass/fail condition, and a deadline or
hour budget. If the work also maps to a roadmap board, read that overlay and
map it to a slot there too.

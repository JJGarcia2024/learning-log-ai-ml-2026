# Stage 7 on the general track

Base shipping checklist for any software. A roadmap overlay adds the board update.

## "Shipped" means, by project type

| Type | Shipped |
|---|---|
| Web app | Deployed to a public URL from `main`; smoke-tested there |
| Mobile app | Signed build installable by testers (APK, TestFlight, internal track) or published to a store |
| Backend/API | Deployed or runnable with one command (e.g. `docker compose up`); docs reachable |
| AI/ML model | Model artifact + eval results + model card/README committed; reproducible with one command |
| CLI / library | Tagged release; installable from the package index or the repo |
| Data pipeline | Scheduled in its runtime; first scheduled run succeeded |

## Every project

1. Merge to `main` (via a PR when the repo uses them), with CI green where CI exists.
2. Tag a version or note the commit hash that shipped.
3. Update the README with what shipped and any known limits ("Where this fails").
4. If the work is on a board (roadmap overlay), update its row; if not, note
   it in the project's own changelog or README.

## Gate to advance

The shipped artifact is reachable by someone other than you, and the board or
changelog reflects it.

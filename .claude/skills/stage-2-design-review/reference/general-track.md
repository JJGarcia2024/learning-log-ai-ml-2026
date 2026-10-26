# Stage 2 on the general track

Base attack checklist for any software. A roadmap overlay adds its own rows.

## Ask of every design

- **Simpler option skipped?** Could a static page, a spreadsheet, an existing
  library, or a managed service do this?
- **Failure paths:** what happens on bad input, no network, empty state,
  slow dependency, duplicate request?
- **Data:** where does state live, who owns it, how is it migrated or backed
  up? Is any personal data stored that doesn't need to be?
- **Security:** secrets out of code, input validated at the boundary, auth
  checked server-side, not only in the UI.
- **Testability:** can the core logic be tested without the UI, network, or GPU?

## Extra attacks by project type

| Type | Attack these first |
|---|---|
| Web app | Is a framework/SPA needed, or would server-rendered pages do? Accessibility (keyboard, contrast, labels)? |
| Mobile app | Cross-platform vs native justified? Behavior when backgrounded, offline, or on a small screen? |
| Backend/API | Error contract consistent across every error? Validation strict where it matters? N+1 queries? |
| AI/ML model | Is the test set leaking into training or tuning? Is the baseline fair? Would a non-ML rule work? |
| CLI / library | Is the public API minimal? Breaking-change policy? Clear exit codes and error messages? |
| Data pipeline | Is it idempotent on re-run? What alerts when it silently produces nothing? |

## Gate to advance

Every applicable item above was asked out loud and either answered or the
spec changed.

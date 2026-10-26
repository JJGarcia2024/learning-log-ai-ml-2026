# Stage ∞ on the general track

Base failure classes for any software. Hypothesize from these first. A roadmap
overlay adds domain-specific ones.

## Across all types

- Works locally, fails elsewhere: missing env var, different runtime version,
  unpinned dependency, case-sensitive file paths.
- Stale state: cache, old build artifact, un-migrated database.
- Swallowed error: a broad `except` or `catch` hiding the real failure.
- Time and order: timezones, race conditions, flaky tests that depend on order.

## By project type

| Type | Likely root causes |
|---|---|
| Web app | CORS, hydration mismatch, stale service worker, env var missing in the hosted build |
| Mobile app | Permission denied path untested, lifecycle (backgrounded/killed), OS-version-specific API, release build differs from debug |
| Backend/API | Validation drift between model and handler, N+1 queries, connection pool exhaustion |
| AI/ML model | Data leakage, train/serve preprocessing mismatch, non-determinism (unseeded), silent dtype or shape broadcast |
| CLI / library | Path handling on Windows vs POSIX, encoding, breaking change in a dependency |
| Data pipeline | Schema drift upstream, non-idempotent re-run, partial load on failure |

## Maintenance beyond bugs

- Dependency updates on a schedule, with tests run after each.
- Re-run the Stage 4 checks periodically, not only when something breaks.
- When a project slips its deadline, cut scope rather than moving the date.
  The human decides which.

## Gate to advance

Root cause named with reasoning, and a regression test added.

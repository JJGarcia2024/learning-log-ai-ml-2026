# Stage 6 on the general track

Base second-review checklist for any software. A roadmap overlay adds rows.

## Re-check, don't trust

- Re-run the Stage 4 check from a clean state (fresh checkout, fresh install,
  empty database) and show the output.
- Do comments claim a reason the code doesn't implement?
- Does the fix suppress a symptom (retry, try/except, `!important`, sleep)
  instead of fixing a cause?
- Are there new warnings, skipped tests, or TODOs the build pass left behind?

## Production checklist (name-check each)

- Errors handled and shown to the user in plain words; nothing leaks a stack trace.
- No secrets, keys, or personal data committed.
- Env vars documented; app fails clearly when one is missing.
- README says how to install, run, and test.

## By project type

| Type | Re-check live |
|---|---|
| Web app | Click through the deployed URL, not localhost; check on a phone-width screen |
| Mobile app | Install the build on a device; kill and reopen the app; try offline |
| Backend/API | Hit the running service with the invalid-input table; check logs for leaked input |
| AI/ML model | Re-run eval; confirm reported numbers match the current run, not an older one |
| CLI / library | Install into a fresh environment and run the fixtures |
| Data pipeline | Re-run twice on the same batch and confirm no duplicates |

## Gate to advance

Actual output shown for every re-check, and the human makes the ship call.

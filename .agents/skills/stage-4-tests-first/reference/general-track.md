# Stage 4 on the general track

Base validation targets for any software. If a roadmap overlay states a
check, it wins — copy it verbatim.

## Write this check first, by project type

| Type | Check | Typical tool |
|---|---|---|
| Web app | One end-to-end user flow passes in a real browser; accessibility score threshold | Playwright/Cypress, Lighthouse |
| Mobile app | One flow passes on an emulator or device; state survives restart | Espresso/XCUITest, Detox, Flutter integration tests |
| Backend/API | Table of valid/invalid requests → expected status + exact error body | pytest + TestClient, Jest + supertest |
| AI/ML model | Metric on held-out test set beats baseline by a stated margin | pytest assertion on eval script output |
| CLI / library | Fixture inputs → expected outputs and exit codes | pytest, snapshot tests |
| Data pipeline | Fixture batch → expected rows; bad batch → loud failure | pytest, data-quality assertions |

## Standing rules

- The threshold is chosen **before** building, not set after measuring.
- The check must fail now. One that passes against nothing is testing the
  wrong thing.
- Prefer behavior checks (what the user sees) over implementation checks.

## Gate to advance

A written, currently failing check whose pass/fail a stranger could verify.

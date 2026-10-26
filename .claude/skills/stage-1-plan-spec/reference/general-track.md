# Stage 1 on the general track

Base spec content for any software. A roadmap overlay (`aiml-track.md`,
`aerospace-track.md`) adds a board slot and gate on top of this.

## What the spec should pin down, by project type

| Type | Pin down |
|---|---|
| Web app | Pages/routes, user flows, auth (or explicitly none), data stored and where, target browsers, hosting |
| Mobile app | Platforms (iOS/Android/both), minimum OS version, offline behavior, permissions needed, how it's installed for testing |
| Backend/API | Endpoints, request/response models, error shape, auth, persistence, rate or latency limits |
| AI/ML model | Dataset and its split, baseline to beat, metric, compute budget, where the model runs (local, API, device) |
| CLI / library | Commands or public API, input/output formats, supported Python/Node/etc. versions, install path |
| Data pipeline | Sources, schedule, schema, idempotency, what happens on bad input |

## Spec template

```
Goal (from Stage 0): ...
Slot: <roadmap row, or "none — personal project"> — time budget: ... — deadline: ...
Users / consumers: ...
Inputs: ...
Outputs / deliverable: ...
Constraints: (platform, versions, budget, privacy, performance)
Validation target (formalized in Stage 4): ...
Explicitly out of scope: ...
```

## Check before advancing

Every ⚑ ambiguity was raised with the human, and the validation target is
concrete enough that a stranger could run it and agree on pass/fail.

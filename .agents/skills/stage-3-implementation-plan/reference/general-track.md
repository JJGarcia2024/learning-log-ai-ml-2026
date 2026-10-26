# Stage 3 on the general track

Base sub-task ordering for any software. A roadmap overlay may refine it.

## Default order: contracts → core → edges → surface → ship

1. **Contracts first:** data models, API schemas, function signatures — the
   shapes everything else depends on.
2. **Core logic:** pure functions/classes, testable without I/O.
3. **Edges:** persistence, network calls, file I/O, external services.
4. **Surface:** UI screens, CLI commands, or HTTP routes wired to the core.
5. **Ship:** packaging, deploy config, README.

The Stage 4 check for each sub-task is written before that sub-task's code.

## Typical orderings by project type

- **Web app**: data model → API/server actions → one page end-to-end →
  remaining pages → styling/accessibility pass → deploy.
- **Mobile app**: data model + local storage → one screen end-to-end →
  navigation → offline/sync → platform permissions → test build on a device.
- **Backend/API**: request/response models → errors + handlers → route logic
  → persistence → auth → docs.
- **AI/ML model**: data loading + split → baseline → metric harness → model →
  training → evaluation on held-out set → packaging/serving.
- **CLI / library**: public API/commands → core → I/O → packaging → docs.
- **Data pipeline**: schema → extract → transform (pure) → load → scheduling
  → alerting.

## Gate to advance

An approved ordered list with one sub-task in flight, and the likely
bottleneck (performance, unfamiliar tech, external dependency) named.

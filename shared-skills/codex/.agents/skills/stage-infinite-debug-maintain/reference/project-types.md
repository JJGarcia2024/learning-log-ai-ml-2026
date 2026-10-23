# Stage ∞ — likely root causes by project type

Hypothesize from these first:

| Project type | Likely root causes |
|---|---|
| Python module | Type hints present but not enforced by a checker; silent `except:` swallowing the real error |
| API service | Pydantic model out of sync with the route logic; generated docs stale relative to the models |
| RAG / LLM app | Chunk boundary splitting facts across chunks (retrieval finds neither half); metric drop after a corpus update that wasn't re-embedded |
| Agent workflow | Graph state not matching a tool's schema after a parameter change; agent looping because a tool doesn't signal terminal state |
| Full-stack UI | Streaming works locally, breaks in deployment (buffering/proxy); env var present locally, missing in the deployed target |
| Simulation / scientific code | Units mismatch; wrong boundary condition; solver not converged; grid too coarse for the regime |

## Maintenance beyond bugs

- Schedule recurring checks for things that drift: exam availability and
  pricing, dependency and framework versions, deprecated APIs, expiring
  credentials.
- Treat schedule risk like a bug: reproduce (what slipped), hypothesize,
  isolate, fix (cut scope inside the sprint, don't move the deadline), harden
  (add a checkpoint). The human decides whether to apply the fix.

## Gate to advance

Root cause named with reasoning (not just "fixed it"), and a regression
check added.

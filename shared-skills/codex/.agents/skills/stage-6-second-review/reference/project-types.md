# Stage 6 — re-check list by project type

| Project type | Re-check this, don't just trust it |
|---|---|
| Python module | Was "written without reference" actually tested that way, or built with reference and retroactively claimed? |
| API service | Does the service rebuild from memory, or does the author need the code open to explain it? |
| RAG / LLM app | Is the eval CI pipeline green right now (re-run it)? Are the README numbers from the current run, not a stale one? |
| Agent workflow | Run the real end-to-end natural-language request live — don't accept a description of what it would do |
| Full-stack UI | Click through the deployed UI with real streaming, not a local-only demo |
| Simulation / scientific code | Does the re-run output match the numbers quoted in the write-up/commit? Do comments claim physics the code doesn't implement? |
| Exam / certification | Was the exam actually passed, not just sat or scheduled? |

## Production-checklist items

Error handling, env vars not hardcoded, basic accessibility on any UI, CI
green on the current commit — verified here, not assumed from earlier stages.

## Gate to advance

Every applicable row was actually re-run or re-checked live, not recalled
from memory of the build pass.

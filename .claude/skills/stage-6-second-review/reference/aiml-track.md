# Stage 6 on the AI/ML track

Second-review checklist by sprint deliverable:

| Sprint | Re-check this, don't just trust it |
|---|---|
| S1 Python | Was the "written without reference" gate actually tested that way, or built with reference and retroactively claimed? |
| S2 FastAPI | Does the service actually rebuild from memory (the sprint's own gate), or does the human need the code open to explain it? |
| S3 AWS AIF-C01 | Was the exam actually PASSED, not just "sat" — status dropdowns distinguish these; don't let "Scheduled" read as "Passed" |
| S4 RAG flagship | Is the DeepEval CI pipeline actually green right now (re-run it), and are the Ragas numbers in the README the current run's numbers, not a stale earlier one? |
| S5 Agentic agent | Run the actual end-to-end natural-language request through the agent live — don't accept a description of what it would do |
| S6 Full-stack | Click through the deployed Next.js chat UI with real streaming, not a local-only demo |

## Production-checklist items (name-checked here, not skipped)

Error handling, env vars not hardcoded, basic accessibility on the Next.js
UI — Essential #8 (Git/GitHub hygiene + CI basics) is verified here, not
assumed from S1–S5.

## Gate to advance

Every applicable row above was actually re-run or re-checked live, not
recalled from memory of the build pass.

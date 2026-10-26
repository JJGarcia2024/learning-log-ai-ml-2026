# Stage 6 on the AI/ML track

Second-review checklist by month row (**AI Systems Engineering Roadmap:
Hardened Edition**):

| Month | Re-check this, don't just trust it |
|---|---|
| M1 Python & Autograd | Re-run pytest and show output; is the module actually on `main`, not just a branch? Does the loss curve in the repo come from the current code? |
| M2 Containers & CI | Re-measure the image size; is CI green on the latest push right now? Was AIF-C01 actually **passed**, not just scheduled? |
| M3 Repo 1 RAG | Re-run the DeepEval CI job; are the README numbers from the current run, not a stale one? |
| M4–M5 Scientific ML | Re-run the analytic check and the out-of-distribution eval from a clean checkout |
| M6–M8 GPU benchmarks | Is the named GPU in the README the one the numbers came from? Were runs within budget? |
| M9 Ray | Was SAA-C03 **passed** (Milestone CERT-1)? Does the Ray Serve demo scale replicas live? |
| M10 Repo 3 Agent | Run the 100-run benchmark live — don't accept a description of what it would do |
| M11–M12 | Can every reported metric be defended in one plain sentence without notes? |

## Production-checklist items (name-checked here, not skipped)

Error handling, env vars not hardcoded, secrets out of the repo, "Where this
fails" section present — verified here, not assumed from the build pass.

## Gate to advance

Every applicable row above was actually re-run or re-checked live, not
recalled from memory of the build pass.

# Stage 2 — attack checklist by project type

| Project type | Attack these first |
|---|---|
| Python module | Are type hints actually enforced (mypy/pyright) or decorative? Does "written without reference" get verified, or just claimed? |
| API service | Do the Pydantic models validate the right things? Are the docs generated from the models, or hand-written and liable to drift? |
| RAG / LLM app | Is the chunking strategy justified for this corpus (not copy-pasted defaults)? Is hybrid search needed, or is vector-only simpler and sufficient? Do the chosen metrics catch hallucination, or just measure retrieval? |
| Agent workflow | Does the graph topology match the real control flow, or is it over-engineered for a linear pipeline? Are tool I/O schemas tight enough to reject malformed parameters before they reach the tool? |
| Full-stack UI | Is streaming actually required, or added complexity? Does the write-up overstate what the repos demonstrate? |
| Simulation / scientific code | Right governing equation? Right numerical method for the regime? Missed edge case? A simpler solver skipped? |
| Exam / certification | Does prep scope match the real exam blueprint, or drift into general trivia? |

## Gate to advance

Every applicable row above has been asked out loud, not silently assumed fine.

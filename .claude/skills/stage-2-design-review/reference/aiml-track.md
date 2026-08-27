# Stage 2 on the AI/ML track

Attack checklist by sprint deliverable (`Wordsmith_Essentials_Sprint_Dec31`):

| Sprint | Attack these first |
|---|---|
| S1 Python | Are the type hints actually enforced (mypy/pyright) or decorative? Does "written without reference" get verified, or just claimed? |
| S2 FastAPI | Pydantic models validating the right things? Swagger docs generated, not hand-written and liable to drift? |
| S3 AWS AIF-C01 | Is prep scope matching the actual exam blueprint, or drifting into general AWS trivia? |
| S4 RAG flagship | Chunking strategy justified for this corpus (not copy-pasted defaults)? Is hybrid search actually needed, or is vector-only sufficient and simpler? Do the chosen Ragas metrics actually catch hallucination, or just measure retrieval? |
| S5 Agentic agent | Does the LangGraph topology match the sim's actual control flow, or is it over-engineered for a linear pipeline? Are tool I/O schemas (Pydantic) tight enough to catch malformed sim parameters before they reach the sim? |
| S6 Full-stack + apps | Is streaming actually required for this UI, or added complexity? Does the resume/app-pack framing overstate what the repos actually demonstrate? |

## Standing question for every DUAL-tagged item

Does the design serve the Deloitte-anchor job stack on its own merits, or is
the aerospace payoff being used to justify a choice that's weaker for the
job track? DUAL should be a bonus, not a design rationalization.

## Gate to advance

Every applicable row above has been asked out loud, not silently assumed
fine.

# Stage 5 — build conventions by project type

- **Python module**: full type hints, docstrings, explicit error handling —
  no bare `except:`.
- **API service**: Pydantic models for every request/response, docs generated
  from the models (not hand-maintained), tests alongside routes.
- **RAG / LLM app**: cite chunking/embedding choices in comments (why this
  chunk size for this corpus); keep the eval harness runnable as its own
  script, not buried in notebook cells.
- **Agent workflow**: pin the framework and dependency versions explicitly;
  tool I/O schemas should reject malformed parameters, not pass them through.
- **Full-stack UI**: implement streaming end-to-end (backend to UI), not
  faked with a spinner; keep the UI shell minimal — it proves full-stack
  capability, it isn't a product.
- **Simulation / scientific code**: comment each non-trivial line with the
  quantity and the physical reason (units, governing law), never a syntax
  restatement. State the result and what it validates after each code block.

## Git hygiene (applies to every project)

Clean commit history, README-first repos, one green CI pipeline. A repo
without these doesn't count as shipped.

## Gate to advance

Code follows the conventions above and CI (where applicable) is green, not
just "should pass".

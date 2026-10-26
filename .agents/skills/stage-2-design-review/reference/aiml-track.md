# Stage 2 on the AI/ML track

Attack checklist by month row (**AI Systems Engineering Roadmap: Hardened
Edition**, `Monthly Timeline` tab):

| Month | Attack these first |
|---|---|
| M1 Python & Autograd | Are strict schemas strict where it matters (JSON-input column of the conversion table, not the Python column)? Do custom handlers leak raw input back in errors? Is the training loop truly hand-written, or a wrapper in disguise? |
| M2 Containers & CI | Is the multi-stage build actually shrinking the image, or copying the whole build env? Does CI run the real tests or a smoke subset? |
| M3 Repo 1 RAG | Is the chunking comparison fair (same corpus, same queries)? Does the p95 <15 ms number include embedding the query, or only the vector lookup? Does faithfulness >0.85 catch hallucination, or just measure retrieval? |
| M4 Operators & Solvers | Is the Burgers benchmark really analytic, or another numerical solver? Are spectral-layer tests checking shape only, or values too? |
| M5 Surrogate & PySR | Is the out-of-distribution set actually out of distribution, or a reshuffled test split? Is PySR given the answer through the feature set? |
| M6 Repo 2 Serving | Is the FastAPI baseline a fair comparison (same batch, same GPU)? Does the input-range guard cover every input, including NaN/inf? |
| M7 Quantization | Is perplexity measured on the same eval set before/after? Is task accuracy reported, not only perplexity? |
| M8 vLLM | Is speculative-decoding speed-up measured at 1 user only, as the DoD states, and not cherry-picked prompts? |
| M9 Ray | Is the Amdahl prediction made *before* the measurement? |
| M10 Repo 3 Agent | Does the state machine stay deterministic (LLM only plans/selects tools)? Are step and token caps enforced in code, not prompt text? |
| M11–M12 | Does any README or Loom claim more than the benchmark tables show? |

## Standing question for dual-track items

Does the design serve the H2 2027 Israeli-startup job gate on its own merits,
or is the Technion/aerospace payoff being used to justify a choice that's
weaker for the job track? Dual-track should be a bonus, not a design
rationalization.

## Gate to advance

Every applicable row above has been asked out loud, not silently assumed
fine.

# Stage 0 on the AI/ML track

Source board: **AI Systems Engineering Roadmap: Hardened Edition (2026–2027)**
— Google Sheet `1IzbsUqNhWxBsgNkXoZEwVuB1mZLEe1u1GG25Nps1o9w`, tab
`Monthly Timeline`. One row per month (M1 Oct 2026 → M12 Sep 2027), each with
a Home Deep-Tech lane, an Onsite AWS/PH lane, a Math Inside slot, and a
**Definition of Done (verifiable DoD)** bullet list.

Pacing: 22–24 h/mo home (25 h ceiling) + 8 h/mo onsite + ~2 h math, measured
in Week 3. 424 h total (312 home • 112 onsite).

## Precedence with the Master Roadmap (split by lane)

This board overlaps the **AI/ML × Aerospace Engineering Master Roadmap
(2026–2036)** (`aerospace-track.md`) for Oct 2026 – Sep 2027:

- **This board wins** for the AI/ML and AWS lanes through Sep 2027.
- **The Master Roadmap wins** for the Math/Physics lane (its 13 h/mo of
  Anton Calculus I and MIT 8.01), and for everything from Oct 2027 on.

## Where a new goal has to land

Every new goal must trace to **one DoD bullet on one month row** (or a Buffer
row). Don't scope work that doesn't serve the **H2 2027 Israeli Startup
Job-Ready Gate (Section 8.1, 2027-09-30)**. The Technion M.Sc. horizon
(March 2034) is a long-range target, not a gate for this board.

**Month rows** (theme • home hrs):

| Row | Theme | Home h |
|---|---|---|
| M1 Oct 2026 | Production Python & Autograd (FastAPI/Pydantic v2 hardening + PyTorch internals) | 22 |
| M2 Nov 2026 | Containers, CI/CD, MLOps • AIF-C01 exam | 22 |
| M3 Dec 2026 | Vector DB & Flagship **Repo 1** (Zotero RAG) | 24 |
| M4 Jan 2027 | Operator Learning & Custom Solvers (FNO/DeepONet, Burgers, Darcy HDF5) | 22 |
| M5 Feb 2027 | Surrogate Training, Physics Loss & PySR | 22 |
| M6 Mar 2027 | Flagship **Repo 2**: Serve-a-Surrogate (ONNX + Triton) | 24 |
| M7 Apr 2027 | LLM Quantization (AWQ/FP8) & KV Cache | 22 |
| M8 May 2027 | vLLM High-Throughput & Speculative Decoding | 22 |
| M9 Jun 2027 | Ray Distributed Systems • SAA-C03 exam | 22 |
| M10 Jul 2027 | Flagship **Repo 3**: Simulation Agent | 24 |
| M11 Aug 2027 | Cross-Repo Benchmarking & Interview Prep | 22 |
| M12 Sep 2027 | Pitch Artifacts (Looms) & Dugri Outreach | 22 |
| Buffer rows | Open-source PR • Mock interrogation • Blank catch-up weeks | 6 / 6 / 18 |

**Job-Ready Gate (Section 8.1), all 7:** 3 repos with results tables & "Where
this fails" • 2 Looms (Repo 2 & 3) • 1 merged open-source PR • AIF-C01 +
SAA-C03 passed • 5 mock technical interviews • 30 civilian startup CTO target
list • one-sentence metric defense ready.

## Falsifiable-goal examples for this track

- Bad: "get good at RAG"
- Good: "Retrieval p95 latency <15 ms on a ≥5,000-chunk corpus, with DeepEval
  faithfulness >0.85 validated in GitHub Actions CI" — this is M3's DoD,
  verbatim, and it's why it's a good goal: it's already falsifiable.

Most DoD bullets are already falsifiable — when a goal maps to one, quote it
rather than rewording it softer.

## Check before advancing

Does the goal map to a specific DoD bullet on a month row, with that row's
home-hour budget and target date known? If not, it isn't scoped yet — go back
to the human, don't invent a row for them. Read the live sheet for current
Status; don't assume from this file.

## Dual-track goals

The header lists two targets: H2 2027 Israeli startups (the gate) and the
March 2034 Technion M.Sc. (aerospace plasma & propulsion, Scientific ML ladder).
Months M4–M6 and M10 serve both. When a goal serves both, note the Technion
payoff explicitly, but keep the falsifiable claim itself gated by the month's
own DoD. Don't let dual-track turn into two goals.

# Codex version

Thirteen skills in the open Agent Skills format (`SKILL.md` folders), which
Codex reads from `.agents/skills/`:

- **An 8-stage build workflow** (`stage-0-brainstorm` … `stage-7-ship`) plus
  `stage-infinite-debug-maintain`, which applies whenever something breaks.
- **Helpers**: `coder-skill`, `devops-git`, `python-lesson-saver`,
  `python-code-snippet-saver`.

Plus an optional `AGENTS.md` with a short always-on working agreement.

## Install (pick one)

**For one project** (best for starting out): copy the `.agents` folder into the
root of your project.

```bash
# macOS / Linux / Git Bash
git clone https://github.com/JJGarcia2024/learning-log-ai-ml-2026.git /tmp/shared
cp -r /tmp/shared/shared-skills/codex/.agents  ./your-project/
cp    /tmp/shared/shared-skills/codex/AGENTS.md ./your-project/   # optional
```

```powershell
# Windows PowerShell
git clone https://github.com/JJGarcia2024/learning-log-ai-ml-2026.git $env:TEMP\shared
Copy-Item -Recurse $env:TEMP\shared\shared-skills\codex\.agents .\your-project\
Copy-Item $env:TEMP\shared\shared-skills\codex\AGENTS.md .\your-project\
```

**For every project**: copy the skill folders into `~/.agents/skills/`
instead. Check OpenAI's Codex "Agent Skills" docs for the current user-level
location before relying on this.

Open a **new** Codex session afterwards so it re-scans the skills.

## Use

Start Codex in your project and ask for a stage by name:

> Use the stage-0-brainstorm skill. I want to build a small API that
> summarizes my notes.

Codex also matches skills to your request by their descriptions, so "I want to
build X" should start at Stage 0 on its own. Move one stage at a time and
confirm each gate.

## Not included

`learn-video` (watch a tutorial video and save notes) is left out because it
depends on a Claude-only video plugin.

## Heads-up

These were written for Claude Code and then generalized. The skill format is
shared, but I could not open OpenAI's docs while preparing this, so confirm
the folder locations and how to name a skill in your Codex version.

If Codex feels like too much right now, use the ChatGPT Project version in
`../chatgpt-project/` instead. Using Google's tools? See `../google/`.

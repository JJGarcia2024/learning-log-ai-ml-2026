# Google (Gemini) versions

Pick the route that matches how your friend uses Gemini. All three are the
same toolkit: an 8-stage build workflow plus Python/Git helpers.

| She uses… | Go to |
|---|---|
| The Gemini app / website (chat) | [A. Gemini app](#a-gemini-app-skills-in-chat) |
| Gemini CLI in a terminal | [B. Gemini CLI](#b-gemini-cli) |
| Google Antigravity | [C. Antigravity](#c-antigravity) |

## A. Gemini app (Skills in chat)

The closest thing to a Claude skill: Gemini's own **Skills**, which import the
same `SKILL.md` format.

**Easiest: one skill.**

1. Download [`chat/zips/build-coach.zip`](chat/zips/build-coach.zip) (on
   GitHub: open the file, then the download button).
2. In Gemini, open **Skills** (sidebar, Customize section; some versions put
   it under Settings) and choose **Upload**. Pick the zip, review, then
   **Create**. Check the skill is switched on.
3. In a chat, type `/build-coach` and describe what you want to build, or just
   say "I want to build X" and Gemini may apply it automatically.

**Closest to Claude skills: the individual ones.** `chat/zips/` also has one
zip per skill (`stage-0-brainstorm.zip` … `stage-7-ship.zip`,
`stage-infinite-debug-maintain.zip`, `coder-skill.zip`, `devops-git.zip`,
`python-lesson-saver.zip`, `python-code-snippet-saver.zip`). Upload them one at
a time. Upload all the `stage-*` ones together, because each hands off to the
next. You don't need both `build-coach` and the individual skills.

**Good to know**

- Skills are rolling out in phases, first for adults on personal accounts.
  Work and school accounts come later, so she may not see Skills yet.
- Skill names must be lowercase with hyphens. The upload cap is 100 MB.
  `.docx` and `.xlsx` files aren't supported inside a skill.
- Skills can't be combined with Canvas or Deep Research.
- If a zip is rejected, Google says you can also upload the unzipped folder or
  the `SKILL.md` file on its own.
- **Gems are being replaced by Skills**, with personal accounts converting this
  November. If Skills aren't on her account yet, a Gem still works as a
  stopgap: paste
  [`../chatgpt-project/01_project-instructions.md`](../chatgpt-project/01_project-instructions.md)
  into the Gem's instructions and add files `02`–`05` as its knowledge files.

## B. Gemini CLI

Gemini CLI reads the same open-standard folders as Codex, from `.gemini/skills/`
or `.agents/skills/`, so the Codex pack works unchanged. Follow the install
steps in [`../codex/README.md`](../codex/README.md) (copy the `.agents` folder
into your project, or the skill folders into `~/.agents/skills/`).

In a session, `/skills list` shows what was found and `/skills reload` picks up
changes. Gemini CLI asks permission before it activates a skill.

## C. Antigravity

Antigravity reads the same folder format from its own locations:

- **One project:** copy the skill folders into `<project>/.agent/skills/`
  (note: `.agent`, singular).
- **Every project:** copy them into `~/.gemini/antigravity/skills/`. One Google
  codelab lists a different global folder (`~/.gemini/config/skills/`), so check
  the docs for your version.

```bash
# from a clone of this repo, into your project (macOS / Linux / Git Bash)
mkdir -p ./your-project/.agent
cp -r shared-skills/codex/.agents/skills ./your-project/.agent/skills
```

```powershell
# Windows PowerShell
New-Item -ItemType Directory -Force .\your-project\.agent | Out-Null
Copy-Item -Recurse shared-skills\codex\.agents\skills .\your-project\.agent\skills
```

Restart the agent session afterwards so Antigravity re-scans the skills.
`AGENTS.md` is optional here: I couldn't confirm that Antigravity reads it.

## Heads-up

These were written for Claude Code, then generalized. I have not run them in
Gemini, Gemini CLI or Antigravity. The upload flow, menu names and folder
paths come from Google's Help pages and secondary write-ups that I could only
read through search summaries. If something doesn't match, trust Google's
current docs over this page.

`learn-video` is left out because it depends on a Claude-only video plugin.

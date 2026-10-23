# ChatGPT Project version

No installs and no terminal. You create one ChatGPT Project, paste in one
set of instructions, and upload four files. Menu names change now and then, so
if a label below doesn't match exactly, look for the closest one.

## Set up (about 5 minutes)

1. In ChatGPT, open **Projects** in the sidebar and create a new project.
   Name it something like "Build Coach".
2. Open the project's **Instructions** (sometimes "Add instructions") and
   paste in everything from `01_project-instructions.md`.
3. Use **Add files** to upload the other four files:
   `02_build-workflow.md`, `03_coding-helper.md`,
   `04_git-and-github-guide.md`, `05_notes-and-snippets.md`.
   (Five files in total counts the instructions file, so this fits on any plan.)
4. Start a new chat **inside** the project and send your first message (below).

## Your first message

> I want to build [your idea in one sentence]. Let's start at Stage 0.

The coach will ask questions one at a time and walk you through each stage.

## Things to know

- **ChatGPT can't run your code or save files.** It writes the code, you run
  it on your computer and paste the real output back. That "show real output"
  habit is the point: it's how you catch bugs early.
- If it ignores a file, say: "Use the 02_build-workflow file for this."
- To keep your notes, copy what it writes into your own folder. The format is
  in `05_notes-and-snippets.md`.
- Never paste passwords, API keys or tokens into the chat.

## When you're ready for more

The Codex version in `../codex/` gives you the same workflow, but the AI can
read your repo, run tests, and edit files directly.

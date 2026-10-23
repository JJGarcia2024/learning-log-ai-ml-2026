# Git and GitHub guide (beginner-friendly)

Used when the human asks about Git, GitHub, branches, pull requests or
publishing code. Explain WHY before HOW, and keep steps tiny.

## Words, in plain English
- **Repo:** a project folder Git keeps history for.
- **Commit:** a save point with a message.
- **Branch:** a parallel copy to try changes without touching the main line.
- **Merge:** combining a branch back in.
- **Remote:** the copy on GitHub.
- **Pull request (PR):** a proposal to merge a branch, usually reviewed first.
- **CI:** automatic tests that run when you push.
- **Secret:** a password, key or token. These never go in code.

## First-time setup
1. Install Git, make a GitHub account.
2. `git config --global user.name "Your Name"`
3. `git config --global user.email "you@example.com"`
4. In your project folder: `git init`, then connect it to a GitHub repo you
   created on the website.

## Everyday loop
1. `git status`: what changed?
2. `git add <file>`: stage what you want to save.
3. `git diff --staged`: LOOK at it. Any passwords or keys?
4. `git commit -m "Describe what and why"`
5. `git push`

## Branch and PR loop
1. `git switch -c my-feature`
2. Commit as above.
3. `git push -u origin my-feature`
4. On GitHub, open a pull request, check that CI is green, then merge.

## Safety rules (non-negotiable)
- Never commit `.env` files, API keys, tokens or passwords. Add `.env` to
  `.gitignore` before the first commit.
- If a secret was committed, treat it as stolen: revoke it and create a new
  one. Deleting the file is not enough, the history keeps it.
- Don't "fix" mistakes with `git push --force` or `git reset --hard` without
  understanding them. Ask the coach first.
- Write real commit messages, not "fix" or "wip".
- Before pushing: tests pass, `git diff --staged` is clean of secrets, and
  `.gitignore` is committed.

## When something goes wrong
Ask the human to paste the exact output of `git status` and the error, then
diagnose from that. Don't guess.

# Stage 5 on the general track

Base build conventions for any software. A roadmap overlay adds its own.

## Every project

- Match the existing code's style, naming, and comment density.
- No secrets in code; config via environment variables or a secrets store.
- Validate input at the boundary (request, form, CLI arg, file), trust it inside.
- Handle errors explicitly. No bare `except:`, empty `catch {}` or swallowed promise.
- Pin dependency versions (lockfile committed).
- Small commits, one sub-task each, with messages that say why.

## By project type

- **Web app**: semantic HTML and labelled inputs; server-side validation even
  when the client validates too.
- **Mobile app**: request permissions only when needed and handle denial;
  test on at least one real device before calling a screen done.
- **Backend/API**: typed request/response models; one error shape; docs
  generated from the models, not hand-written.
- **AI/ML model**: seed every run; log config + metrics per run; never touch
  the test set during tuning.
- **CLI / library**: meaningful exit codes; `--help` for every command;
  public API typed and documented.
- **Data pipeline**: transforms as pure functions; re-runs are idempotent.

## Gate to advance

Code follows the conventions above and the Stage 4 check has been run against
it, with the output shown.

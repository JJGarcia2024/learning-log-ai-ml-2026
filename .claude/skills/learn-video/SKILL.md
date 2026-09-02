---
name: learn-video
description: >
  Activate when the user wants to watch a Python tutorial video (usually from
  "Python Fundamental Skills/") and be taught its content, with the lesson
  automatically saved as lecture notes and code snippets. Triggers on:
  "/learn-video <path>", "watch and teach me <video>", "teach me this video
  and save it", or any request to watch a lecture video and save what it
  covers. Combines video watching, teaching, and saving into one step so the
  user doesn't have to chain watch-video + python-lesson-saver +
  python-code-snippet-saver manually.
---

# Learn Video

One-shot pipeline: watch a lecture video, teach its content, and save both
lecture notes and code snippets in this repo's actual folder structure.

## Input

The argument is a video file path (or YouTube URL) and optionally extra
instructions. If no path is given, ask the user for one — do not guess a
path.

## Process

1. **Watch the video.** Follow the same workflow as the `claude-video-vision:watch-video`
   skill:
   - Call `video_info` to confirm the file and get duration.
   - For videos > 30s, call `video_analyze` first (`scene_changes`, `silence`,
     `transcription`) to plan extraction. If the local backend has no
     transcription available (e.g. whisper not installed), proceed with
     `skip_audio: true` and rely on visual frames — do not fail the task
     over missing transcription.
   - Call `video_watch` (short videos: `fps: "auto"`, no `view_sample`) or,
     for longer videos, chunk with `video_watch`/`video_detail` across
     segments and `view_sample` to stay under output limits.
   - If a `video_watch`/`video_analyze` call errors with a setup problem
     (e.g. missing ffmpeg/ffprobe), call `video_setup` and report what's
     missing rather than silently giving up.

2. **Teach the content.** Give the user a clear, structured explanation of
   what the video covers — concepts, the code examples shown on screen, and
   any key takeaways — as if teaching the lesson directly. This happens
   whether or not saving succeeds.

3. **Save lecture notes.** Save to:

   ```
   Python Fundamental Skills\Lecture Notes\<topic>.md
   ```

   (NOT `Lessons/Python Fundamentals/...` — that is a different, unrelated
   folder in this repo.) Match the existing file style already used in that
   folder (plain `# Title` + `**Source:**` line + prose/code blocks — no
   YAML-style frontmatter block). Use snake_case or the topic's natural name
   for the filename (check existing files in the folder for the closest
   match, e.g. `scope.md`, `xargs.md`, `types_of_functions.md`).

   - If a file for this exact topic already exists, merge/append the new
     material into it with a `---` separator instead of creating a
     near-duplicate file.

4. **Save code snippets.** Save to:

   ```
   Python Fundamental Skills\Code Snippets\<topic>.py
   ```

   Match the existing header style used in that folder (a short `# Topic:` /
   `# Subtopic:` / `# Source:` / `# Description:` comment block, then
   `# --- Code ---`, with runnable example code and `# ---` separators
   between examples). Use the same filename stem as the matching lecture
   note (e.g. `scope.py` alongside `scope.md`).

   - If a file for this exact topic already exists, append the new snippet
     with a `# ---` separator instead of creating a near-duplicate file.

5. **Confirm.** Tell the user the exact paths saved to (or updated).

## Notes

- Always check `Python Fundamental Skills\Lecture Notes\` and
  `Python Fundamental Skills\Code Snippets\` for an existing file on the
  topic before creating a new one — this repo prefers one file per topic
  over many overlapping ones.
- If the video was already watched earlier in the conversation, don't
  re-watch it — reuse what's already known and go straight to teaching +
  saving.

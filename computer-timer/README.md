# Spacesmith's Timer — Windows Edition

A desktop version of the Android productivity timer. It cycles through four
phases on repeat and **auto-starts at 9:00 AM** (as long as your PC is on and
you're logged in), popping up an always-on-top **mini floating window**.

| Phase | Duration | |
|-------|----------|---|
| Upskilling | 20 min | 📚 |
| Eye Rest | 5 min | 😌 |
| Work | 60 min | ⚡ |
| Eye Rest | 5 min | 😌 |

Then it loops forever. An alarm rings at the end of each phase.

---

## Quick start (recommended: standalone .exe)

You only need Python **once**, on the machine where you build the `.exe`.
After that the `.exe` runs on any Windows PC without Python.

1. Install Python 3 from <https://python.org> (tick *"Add Python to PATH"*).
2. Double-click **`build_exe.bat`**.
   - This produces **`dist\SpacesmithTimer.exe`**.
3. Double-click **`install_autostart.bat`**.
   - This makes the app launch automatically every time you log in, so the
     9 AM trigger works without you doing anything.
4. Double-click **`dist\SpacesmithTimer.exe`** to run it now.

That's it. Leave your PC on overnight (or just turn it on before 9 AM) and the
cycle kicks off automatically each morning.

### Don't want to build an .exe?

Just run the script directly (needs Python installed):

```
python spacesmith_timer.py
```

`install_autostart.bat` also works in this mode — it will point the startup
shortcut at the script if no `.exe` is found.

---

## Using the app

- **▶ Start / ⏸ Pause / ↺ Reset / Skip ⤳** — manual control any time.
- **🗗 (top-right)** — shrink to the mini floating window (always on top, like
  Picture-in-Picture). Click **▢** in the mini window to go back to full size.
- **⚙ (top-left)** — Settings:
  - Toggle the alarm sound on/off.
  - Toggle the 9 AM auto-start on/off.
  - Pick your own alarm sound — **`.wav` or `.mp3`** — and **Test** it.
    Leave it blank to use the built-in beep pattern.

Settings are saved to `%APPDATA%\SpacesmithTimer\config.json`.

---

## How the 9 AM trigger works

The app checks the clock every second. The first time it sees that the local
time is **9:00 AM or later and it hasn't already fired today**, it opens the
mini window and starts the cycle from the top. It records the date so it only
fires once per day.

This means:
- If your PC is already on at 9:00 AM → it fires at 9:00.
- If you turn the PC on (or wake it) at, say, 9:40 AM → it fires immediately,
  since it hasn't run yet today. So you never miss the morning cycle.

Because Windows can't launch a closed app on its own, `install_autostart.bat`
makes the app start at login — that's what keeps it running and ready.

### Option B: exact 9:00 launch via Task Scheduler (recommended for precision)

If you'd rather have Windows launch the app at **exactly 9:00 AM** even when it's
fully closed, use Task Scheduler instead of (or in addition to) the login
shortcut:

1. Build the `.exe` first (`build_exe.bat`) — or have Python installed.
2. Double-click **`install_task_scheduler.bat`**.

This registers a daily task named **"Spacesmith's Timer 9AM"** that:
- fires at **9:00 AM** every day, and
- has **"start the task as soon as available"** enabled — so if the PC was off
  at 9:00, it launches the moment you turn the PC on, and
- runs on battery too (no AC-power requirement).

When the app launches this way it immediately sees it's past 9 AM and starts the
cycle in the mini window.

Useful commands (Command Prompt):
```
schtasks /Run    /TN "Spacesmith's Timer 9AM"     REM test it now
schtasks /Delete /TN "Spacesmith's Timer 9AM" /F  REM remove it
```
Or open **Task Scheduler** from the Start menu to view/edit it.

> Pick **one** launch method. Use the Startup shortcut (Option A) *or* the
> Task Scheduler task (Option B) — running both just opens the app twice.
> The `.xml` template (`SpacesmithTimer-9AM.xml`) is filled in with your real
> path by the installer; you don't edit it by hand.

---

## No third-party packages

The app uses only the Python standard library. Audio (including `.mp3`) is
played through Windows' built-in `winmm` component via `ctypes` — nothing to
`pip install`. PyInstaller is used only to build the optional `.exe`.

## Optional: custom icon

Drop an `astronaut.ico` file next to `spacesmith_timer.py` and it will be used
for both the window and the built `.exe`.

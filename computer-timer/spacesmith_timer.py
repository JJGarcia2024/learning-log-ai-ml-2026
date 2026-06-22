"""
Spacesmith's Timer  -  Windows edition
=======================================
A productivity cycle timer that auto-starts at 9:00 AM (as long as the PC is on),
pops up an always-on-top mini "floating" window, and rings an alarm between phases.

Cycle (repeats forever):
    📚 Upskilling   20 min
    😌 Eye Rest      5 min
    ⚡ Work         60 min
    😌 Eye Rest      5 min

No third-party packages required. Audio uses Windows' built-in winmm (MCI),
which plays both .wav and .mp3. A simple beep pattern is the fallback.

Run:   python spacesmith_timer.py
Build: see build_exe.bat  (creates a standalone SpacesmithTimer.exe)
"""

import os
import sys
import json
import ctypes
import datetime
import threading
import tkinter as tk
from tkinter import ttk, filedialog

# --------------------------------------------------------------------------- #
#  Theme (blue / gold space palette, matching the Android app)
# --------------------------------------------------------------------------- #
DEEP_NAVY   = "#0A1628"
SURFACE     = "#0D2137"
SURFACE_2   = "#132033"
GOLD        = "#FFD700"
GOLD_DIM    = "#F9A825"
BLUE        = "#1565C0"
BLUE_LIGHT  = "#90CAF9"
TEXT        = "#E8EEF7"
TEXT_DIM    = "#8FA3BF"

# --------------------------------------------------------------------------- #
#  Timer cycle definition
# --------------------------------------------------------------------------- #
PHASES = [
    {"name": "Upskilling", "emoji": "📚", "seconds": 20 * 60, "accent": GOLD,       "desc": "Learning & growth"},
    {"name": "Eye Rest",   "emoji": "😌", "seconds":  5 * 60, "accent": BLUE_LIGHT, "desc": "Close eyes — music or ASMR"},
    {"name": "Work",       "emoji": "⚡", "seconds": 60 * 60, "accent": GOLD,       "desc": "Deep focus session"},
    {"name": "Eye Rest",   "emoji": "😌", "seconds":  5 * 60, "accent": BLUE_LIGHT, "desc": "Close eyes — music or ASMR"},
]

AUTO_HOUR = 9          # auto-start at 09:00
AUTO_MINUTE = 0

# --------------------------------------------------------------------------- #
#  Persistent settings
# --------------------------------------------------------------------------- #
def _config_dir() -> str:
    base = os.environ.get("APPDATA") or os.path.expanduser("~")
    path = os.path.join(base, "SpacesmithTimer")
    os.makedirs(path, exist_ok=True)
    return path

CONFIG_PATH = os.path.join(_config_dir(), "config.json")

DEFAULT_SETTINGS = {
    "alarm_enabled": True,
    "alarm_file": "",          # path to .wav/.mp3, or "" for the beep pattern
    "auto_9am": True,          # auto-start the cycle at 9 AM
    "last_auto_date": "",      # YYYY-MM-DD of the last 9 AM auto-trigger
}

def load_settings() -> dict:
    s = dict(DEFAULT_SETTINGS)
    try:
        with open(CONFIG_PATH, "r", encoding="utf-8") as f:
            s.update(json.load(f))
    except Exception:
        pass
    return s

def save_settings(s: dict) -> None:
    try:
        with open(CONFIG_PATH, "w", encoding="utf-8") as f:
            json.dump(s, f, indent=2)
    except Exception:
        pass

# --------------------------------------------------------------------------- #
#  Audio  -  Windows winmm / MCI (plays .wav and .mp3, no dependencies)
# --------------------------------------------------------------------------- #
class Alarm:
    _alias = "spacesmith_alarm"

    def __init__(self):
        self._winmm = None
        if sys.platform == "win32":
            try:
                self._winmm = ctypes.windll.winmm
            except Exception:
                self._winmm = None

    def _mci(self, command: str) -> int:
        if not self._winmm:
            return 1
        return self._winmm.mciSendStringW(command, None, 0, None)

    def play(self, file_path: str):
        """Play a sound file once. Falls back to a beep pattern on any failure."""
        if file_path and self._winmm and os.path.isfile(file_path):
            try:
                self._mci(f'close {self._alias}')
                self._mci(f'open "{file_path}" alias {self._alias}')
                rc = self._mci(f'play {self._alias}')
                if rc == 0:
                    return
            except Exception:
                pass
        self._beep_pattern()

    def stop(self):
        self._mci(f'stop {self._alias}')
        self._mci(f'close {self._alias}')

    def _beep_pattern(self):
        def run():
            try:
                import winsound
                for _ in range(3):
                    winsound.Beep(880, 250)
                    winsound.Beep(660, 250)
            except Exception:
                try:
                    print("\a", end="", flush=True)
                except Exception:
                    pass
        threading.Thread(target=run, daemon=True).start()

# --------------------------------------------------------------------------- #
#  Main application
# --------------------------------------------------------------------------- #
class SpacesmithTimer:
    def __init__(self, root: tk.Tk):
        self.root = root
        self.settings = load_settings()
        self.alarm = Alarm()

        self.phase_index = 0
        self.seconds_remaining = PHASES[0]["seconds"]
        self.is_running = False
        self.is_mini = False
        self._tick_job = None

        root.title("Spacesmith's Timer")
        root.configure(bg=DEEP_NAVY)
        root.minsize(360, 520)
        self._set_window_icon()
        self._center(440, 660)

        self._build_full_ui()
        self._build_mini_ui()
        self.show_full()

        self._update_display()
        self._schedule_tick()
        root.protocol("WM_DELETE_WINDOW", self._on_close)

    # ----------------------------- window helpers ------------------------- #
    def _center(self, w, h):
        self.root.update_idletasks()
        sw = self.root.winfo_screenwidth()
        sh = self.root.winfo_screenheight()
        x = (sw - w) // 2
        y = (sh - h) // 3
        self.root.geometry(f"{w}x{h}+{x}+{y}")

    def _set_window_icon(self):
        try:
            ico = os.path.join(os.path.dirname(os.path.abspath(__file__)), "astronaut.ico")
            if os.path.isfile(ico):
                self.root.iconbitmap(ico)
        except Exception:
            pass

    # ------------------------------ full UI ------------------------------- #
    def _build_full_ui(self):
        self.full = tk.Frame(self.root, bg=DEEP_NAVY)

        top = tk.Frame(self.full, bg=DEEP_NAVY)
        top.pack(fill="x", padx=16, pady=(14, 0))
        tk.Button(top, text="⚙", command=self.open_settings, **self._icon_btn()).pack(side="left")
        tk.Label(top, text="SPACESMITH'S TIMER", bg=DEEP_NAVY, fg=TEXT_DIM,
                 font=("Segoe UI", 10, "bold")).pack(side="left", expand=True)
        tk.Button(top, text="🗗", command=self.show_mini, **self._icon_btn()).pack(side="right")

        # astronaut header
        tk.Label(self.full, text="🧑‍🚀", bg=DEEP_NAVY, fg=TEXT,
                 font=("Segoe UI Emoji", 40)).pack(pady=(18, 2))

        # cycle strip
        self.strip = tk.Frame(self.full, bg=DEEP_NAVY)
        self.strip.pack(pady=(6, 4))
        self.strip_cells = []
        for i in range(len(PHASES)):
            c = tk.Frame(self.strip, width=46, height=6, bg=SURFACE_2)
            c.pack(side="left", padx=3)
            c.pack_propagate(False)
            self.strip_cells.append(c)

        # phase emoji + name
        self.lbl_emoji = tk.Label(self.full, text="📚", bg=DEEP_NAVY, font=("Segoe UI Emoji", 56))
        self.lbl_emoji.pack(pady=(18, 0))
        self.lbl_phase = tk.Label(self.full, text="Upskilling", bg=DEEP_NAVY, fg=TEXT,
                                  font=("Segoe UI", 22, "bold"))
        self.lbl_phase.pack()
        self.lbl_desc = tk.Label(self.full, text="", bg=DEEP_NAVY, fg=TEXT_DIM,
                                 font=("Segoe UI", 11))
        self.lbl_desc.pack(pady=(2, 0))

        # countdown ring
        self.canvas = tk.Canvas(self.full, width=260, height=260, bg=DEEP_NAVY,
                                highlightthickness=0)
        self.canvas.pack(pady=18)
        self.lbl_time = tk.Label(self.full, text="20:00", bg=DEEP_NAVY, fg=GOLD,
                                 font=("Consolas", 44, "bold"))
        self.lbl_time.place(in_=self.canvas, relx=0.5, rely=0.5, anchor="center")

        # controls
        ctr = tk.Frame(self.full, bg=DEEP_NAVY)
        ctr.pack(pady=(4, 18))
        tk.Button(ctr, text="↺ Reset", command=self.reset, **self._text_btn()).grid(row=0, column=0, padx=8)
        self.btn_play = tk.Button(ctr, text="▶  Start", command=self.toggle, **self._primary_btn())
        self.btn_play.grid(row=0, column=1, padx=8)
        tk.Button(ctr, text="Skip ⤳", command=self.skip, **self._text_btn()).grid(row=0, column=2, padx=8)

    # ------------------------------ mini UI ------------------------------- #
    def _build_mini_ui(self):
        self.mini = tk.Frame(self.root, bg=DEEP_NAVY)
        bar = tk.Frame(self.mini, bg=SURFACE)
        bar.pack(fill="x")
        tk.Button(bar, text="▢", command=self.show_full, **self._icon_btn(SURFACE)).pack(side="right")

        self.mini_emoji = tk.Label(self.mini, text="📚", bg=DEEP_NAVY, font=("Segoe UI Emoji", 22))
        self.mini_emoji.pack(pady=(6, 0))
        self.mini_time = tk.Label(self.mini, text="20:00", bg=DEEP_NAVY, fg=GOLD,
                                  font=("Consolas", 30, "bold"))
        self.mini_time.pack()
        self.mini_phase = tk.Label(self.mini, text="UPSKILLING", bg=DEEP_NAVY, fg=TEXT_DIM,
                                   font=("Segoe UI", 9, "bold"))
        self.mini_phase.pack(pady=(0, 4))

        mc = tk.Frame(self.mini, bg=DEEP_NAVY)
        mc.pack(pady=(0, 6))
        tk.Button(mc, text="⏯", command=self.toggle, **self._icon_btn()).pack(side="left", padx=6)
        tk.Button(mc, text="⤳", command=self.skip, **self._icon_btn()).pack(side="left", padx=6)

    # ----------------------------- button styles -------------------------- #
    def _icon_btn(self, bg=DEEP_NAVY):
        return dict(bg=bg, fg=TEXT, activebackground=SURFACE_2, activeforeground=GOLD,
                    bd=0, relief="flat", font=("Segoe UI", 13), cursor="hand2",
                    highlightthickness=0)

    def _text_btn(self):
        return dict(bg=SURFACE_2, fg=TEXT, activebackground=SURFACE, activeforeground=GOLD,
                    bd=0, relief="flat", font=("Segoe UI", 11), cursor="hand2",
                    padx=10, pady=8, highlightthickness=0)

    def _primary_btn(self):
        return dict(bg=GOLD, fg=DEEP_NAVY, activebackground=GOLD_DIM, activeforeground=DEEP_NAVY,
                    bd=0, relief="flat", font=("Segoe UI", 13, "bold"), cursor="hand2",
                    padx=22, pady=10, highlightthickness=0)

    # ------------------------------ view switch --------------------------- #
    def show_full(self):
        self.is_mini = False
        self.mini.pack_forget()
        self.root.overrideredirect(False)
        self.root.attributes("-topmost", False)
        self._center(440, 660)
        self.full.pack(fill="both", expand=True)
        self._update_display()

    def show_mini(self):
        self.is_mini = True
        self.full.pack_forget()
        self.root.attributes("-topmost", True)
        sw = self.root.winfo_screenwidth()
        self.root.geometry(f"220x150+{sw - 246}+40")
        self.mini.pack(fill="both", expand=True)
        self._update_display()

    # ------------------------------ controls ------------------------------ #
    def toggle(self):
        self.pause() if self.is_running else self.start()

    def start(self):
        self.alarm.stop()
        self.is_running = True
        self.btn_play.config(text="⏸  Pause")
        self._update_display()

    def pause(self):
        self.alarm.stop()
        self.is_running = False
        self.btn_play.config(text="▶  Start")
        self._update_display()

    def skip(self):
        self.alarm.stop()
        self._advance_phase()
        self._update_display()

    def reset(self):
        self.alarm.stop()
        self.is_running = False
        self.phase_index = 0
        self.seconds_remaining = PHASES[0]["seconds"]
        self.btn_play.config(text="▶  Start")
        self._update_display()

    def auto_start_cycle(self):
        """Triggered at 9 AM: open the mini window and start from the top."""
        self.phase_index = 0
        self.seconds_remaining = PHASES[0]["seconds"]
        self.show_mini()
        self.start()

    def _advance_phase(self):
        self.phase_index = (self.phase_index + 1) % len(PHASES)
        self.seconds_remaining = PHASES[self.phase_index]["seconds"]

    # ------------------------------- ticking ------------------------------ #
    def _schedule_tick(self):
        self._tick_job = self.root.after(1000, self._tick)

    def _tick(self):
        # 9 AM auto-trigger check
        self._check_auto_start()

        if self.is_running:
            self.seconds_remaining -= 1
            if self.seconds_remaining <= 0:
                if self.settings.get("alarm_enabled", True):
                    self.alarm.play(self.settings.get("alarm_file", ""))
                self._advance_phase()
            self._update_display()

        self._schedule_tick()

    def _check_auto_start(self):
        if not self.settings.get("auto_9am", True):
            return
        now = datetime.datetime.now()
        today = now.strftime("%Y-%m-%d")
        if self.settings.get("last_auto_date") == today:
            return
        # fire if we're at or past 9:00 AM today and haven't fired yet
        if (now.hour, now.minute) >= (AUTO_HOUR, AUTO_MINUTE) and now.hour < 23:
            self.settings["last_auto_date"] = today
            save_settings(self.settings)
            self.auto_start_cycle()

    # ------------------------------- display ------------------------------ #
    def _update_display(self):
        phase = PHASES[self.phase_index]
        mins, secs = divmod(max(0, self.seconds_remaining), 60)
        time_str = f"{mins:02d}:{secs:02d}"
        accent = phase["accent"]

        if self.is_mini:
            self.mini_emoji.config(text=phase["emoji"])
            self.mini_time.config(text=time_str, fg=accent)
            self.mini_phase.config(text=phase["name"].upper())
            return

        self.lbl_emoji.config(text=phase["emoji"])
        self.lbl_phase.config(text=phase["name"], fg=accent)
        self.lbl_desc.config(text=phase["desc"])
        self.lbl_time.config(text=time_str, fg=accent)

        for i, cell in enumerate(self.strip_cells):
            if i < self.phase_index:
                cell.config(bg=accent if False else GOLD_DIM)
            elif i == self.phase_index:
                cell.config(bg=accent)
            else:
                cell.config(bg=SURFACE_2)

        self._draw_ring(accent)

    def _draw_ring(self, accent):
        c = self.canvas
        c.delete("all")
        total = PHASES[self.phase_index]["seconds"]
        frac = 0.0 if total == 0 else 1.0 - (self.seconds_remaining / total)
        x0, y0, x1, y1 = 18, 18, 242, 242
        c.create_oval(x0, y0, x1, y1, outline=SURFACE_2, width=14)
        if frac > 0:
            c.create_arc(x0, y0, x1, y1, start=90, extent=-360 * frac,
                         style="arc", outline=accent, width=14)

    # ------------------------------ settings ------------------------------ #
    def open_settings(self):
        win = tk.Toplevel(self.root)
        win.title("Settings")
        win.configure(bg=DEEP_NAVY)
        win.geometry("380x340")
        win.transient(self.root)
        win.grab_set()

        tk.Label(win, text="Settings", bg=DEEP_NAVY, fg=TEXT,
                 font=("Segoe UI", 16, "bold")).pack(anchor="w", padx=20, pady=(18, 10))

        alarm_var = tk.BooleanVar(value=self.settings.get("alarm_enabled", True))
        auto_var = tk.BooleanVar(value=self.settings.get("auto_9am", True))
        file_var = tk.StringVar(value=self.settings.get("alarm_file", ""))

        def row(parent):
            f = tk.Frame(parent, bg=DEEP_NAVY)
            f.pack(fill="x", padx=20, pady=6)
            return f

        cb_style = dict(bg=DEEP_NAVY, fg=TEXT, selectcolor=SURFACE_2,
                        activebackground=DEEP_NAVY, activeforeground=GOLD,
                        font=("Segoe UI", 11), bd=0, highlightthickness=0,
                        anchor="w")

        r = row(win)
        tk.Checkbutton(r, text="Alarm sound between phases", variable=alarm_var, **cb_style).pack(fill="x")
        r = row(win)
        tk.Checkbutton(r, text="Auto-start cycle at 9:00 AM", variable=auto_var, **cb_style).pack(fill="x")

        tk.Label(win, text="Alarm sound file (.wav / .mp3)", bg=DEEP_NAVY, fg=TEXT_DIM,
                 font=("Segoe UI", 10)).pack(anchor="w", padx=20, pady=(12, 2))
        fr = row(win)
        entry = tk.Entry(fr, textvariable=file_var, bg=SURFACE_2, fg=TEXT, bd=0,
                         insertbackground=TEXT, font=("Segoe UI", 9))
        entry.pack(side="left", fill="x", expand=True, ipady=6, padx=(0, 6))

        def browse():
            p = filedialog.askopenfilename(
                title="Choose alarm sound",
                filetypes=[("Audio", "*.wav *.mp3"), ("All files", "*.*")])
            if p:
                file_var.set(p)

        def test():
            self.alarm.play(file_var.get())

        tk.Button(fr, text="Browse", command=browse, **self._text_btn()).pack(side="left")
        tk.Button(win, text="🔊 Test sound", command=test, **self._text_btn()).pack(padx=20, pady=(4, 0), anchor="w")

        def save_and_close():
            self.settings["alarm_enabled"] = alarm_var.get()
            self.settings["auto_9am"] = auto_var.get()
            self.settings["alarm_file"] = file_var.get().strip()
            save_settings(self.settings)
            self.alarm.stop()
            win.destroy()

        tk.Button(win, text="Save", command=save_and_close, **self._primary_btn()).pack(pady=18)

    # ------------------------------- close -------------------------------- #
    def _on_close(self):
        self.alarm.stop()
        save_settings(self.settings)
        self.root.destroy()


def main():
    # Allow per-monitor DPI awareness for crisp text on Windows
    if sys.platform == "win32":
        try:
            ctypes.windll.shcore.SetProcessDpiAwareness(1)
        except Exception:
            pass
    root = tk.Tk()
    SpacesmithTimer(root)
    root.mainloop()


if __name__ == "__main__":
    main()

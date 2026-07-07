# Sleep Timer — Android Studio project

A native Kotlin + Jetpack Compose port of the Sleep Timer web app. It auto-starts
a countdown during your sleep window (default **9:00 PM → 6:30 AM**) and floats the
timer over other apps using Android's **Picture-in-Picture** mode.

## Features

- ⏰ **Auto-start** — an exact daily alarm fires at the configured sleep time and
  brings the countdown to the foreground, automatically opening the floating window.
- 🪟 **Floating window that coexists with other PiP** — tap **Float Window** to show a
  draggable system overlay (the "Display over other apps" mechanism). Because it is
  **not** an Android Picture-in-Picture window, it happily floats on top of another
  app's PiP (e.g. a YouTube video in PiP) instead of replacing it.
  > Android only allows **one** system PiP window at a time, so two PiP windows can
  > never coexist. The overlay sidesteps that limitation. Native PiP is still offered
  > as a secondary "Use system Picture-in-Picture" option for single-window use.
- 🎨 **Blue & gold theme** with an animated gradient background and progress ring.
- 🌗 **Dark / Light / System** theme modes.
- 🖼️ **Wallpaper** — pick any image and adjust its opacity with a slider.
- 🔤 **Google Sans** typography (see font note below).
- ⚙️ **Configurable** sleep and wake times.

## Open in Android Studio

1. **File → Open…** and select this `android/` folder.
2. Let Gradle sync (Android Studio downloads the Gradle distribution defined in
   `gradle/wrapper/gradle-wrapper.properties` automatically).
3. Run on a device/emulator with **API 26+** (PiP requires Android 8.0+).

> The Gradle wrapper JAR is intentionally not committed. Android Studio generates
> it on first sync. If you prefer the command line, run `gradle wrapper` once, then
> `./gradlew assembleDebug`.

## Google Sans font

Google Sans is proprietary and is **not** bundled. The app falls back to the system
sans-serif so it builds and runs immediately. To use the real font, drop the `.ttf`
files into `app/src/main/res/font/` and follow `app/src/main/res/font/README.md`.

## Permissions

- `SCHEDULE_EXACT_ALARM` / `USE_EXACT_ALARM` — fire the auto-start alarm precisely.
  On Android 12+ tap **Grant exact-alarm permission** in Settings if prompted.
- `SYSTEM_ALERT_WINDOW` — the coexisting floating overlay. The first time you tap
  **Float Window** (or via Settings → *Grant "Display over other apps"*) you'll be
  sent to the system screen to enable it for Sleep Timer.
- `FOREGROUND_SERVICE` / `FOREGROUND_SERVICE_SPECIAL_USE` — keep the overlay alive
  while you're in other apps.
- `RECEIVE_BOOT_COMPLETED` — re-arm the daily alarm after a reboot.
- `READ_MEDIA_IMAGES` — load a wallpaper you choose (via the photo picker).
- `POST_NOTIFICATIONS` — reserved for wake notifications.

## Project layout

```
android/
├─ app/src/main/
│  ├─ AndroidManifest.xml
│  ├─ java/com/jireh/sleeptimer/
│  │  ├─ MainActivity.kt          # Compose UI + PiP
│  │  ├─ FloatingTimerService.kt  # overlay window (coexists with other PiP)
│  │  ├─ TimerEngine.kt           # sleep-window time math
│  │  ├─ SleepPrefs.kt            # persisted settings
│  │  ├─ SleepAlarmScheduler.kt   # daily exact alarm
│  │  ├─ SleepAlarmReceiver.kt    # auto-start at sleep time
│  │  ├─ BootReceiver.kt          # re-arm after reboot
│  │  └─ ui/theme/                # Color, Type, Theme
│  └─ res/                        # icons, strings, font slot
├─ build.gradle.kts
├─ settings.gradle.kts
└─ gradle/wrapper/gradle-wrapper.properties
```

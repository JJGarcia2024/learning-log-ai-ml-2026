# Google Sans font

Google Sans is a **proprietary** typeface and cannot be redistributed in this
repo. To use it:

1. Obtain the font files (e.g. `GoogleSans-Regular.ttf`, `GoogleSans-Medium.ttf`,
   `GoogleSans-Bold.ttf`).
2. Drop them into this `res/font/` folder using lowercase, underscore names:
   - `google_sans_regular.ttf`
   - `google_sans_medium.ttf`
   - `google_sans_bold.ttf`
3. Open `ui/theme/Type.kt` and uncomment the `GoogleSans` `FontFamily` block.

Until then the app falls back to the system sans-serif so it still builds and runs.

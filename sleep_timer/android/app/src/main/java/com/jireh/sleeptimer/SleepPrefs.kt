package com.jireh.sleeptimer

import android.content.Context

/** Simple persisted settings backed by SharedPreferences. */
class SleepPrefs(context: Context) {
    private val sp = context.getSharedPreferences("sleep_timer", Context.MODE_PRIVATE)

    var sleepHour: Int
        get() = sp.getInt("sleep_hour", 21)
        set(v) = sp.edit().putInt("sleep_hour", v).apply()

    var sleepMinute: Int
        get() = sp.getInt("sleep_minute", 0)
        set(v) = sp.edit().putInt("sleep_minute", v).apply()

    var wakeHour: Int
        get() = sp.getInt("wake_hour", 6)
        set(v) = sp.edit().putInt("wake_hour", v).apply()

    var wakeMinute: Int
        get() = sp.getInt("wake_minute", 30)
        set(v) = sp.edit().putInt("wake_minute", v).apply()

    /** "system" | "dark" | "light" */
    var themeMode: String
        get() = sp.getString("theme_mode", "dark") ?: "dark"
        set(v) = sp.edit().putString("theme_mode", v).apply()

    /** Content URI of the chosen wallpaper, or null. */
    var wallpaperUri: String?
        get() = sp.getString("wallpaper_uri", null)
        set(v) = sp.edit().putString("wallpaper_uri", v).apply()

    /** Wallpaper opacity, 0f..1f */
    var wallpaperOpacity: Float
        get() = sp.getFloat("wallpaper_opacity", 0.5f)
        set(v) = sp.edit().putFloat("wallpaper_opacity", v).apply()
}

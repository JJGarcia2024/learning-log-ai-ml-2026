package com.twinklingtreasure.timer.data

data class AppSettings(
    val alarmSoundUri: String   = "default",  // "": off, "default": system alarm, else URI string
    val alarmSoundName: String  = "Default alarm",
    val vibrateEnabled: Boolean = true,
    val wallpaperUri: String    = "",          // "": no wallpaper
    val wallpaperOpacity: Float = 0.35f,       // 0..1, how visible the wallpaper is
    val forceDarkMode: Boolean  = true,
    // Phase durations (minutes); defaults match the original 20-5-60-5 cycle
    val upskillingMinutes: Int  = 20,
    val eyeRest1Minutes: Int    = 5,
    val workMinutes: Int        = 60,
    val eyeRest2Minutes: Int    = 5,
)

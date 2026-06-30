package com.twinklingtreasure.timer.data

data class AppSettings(
    val vibrateEnabled: Boolean = true,
    val wallpaperUri: String    = "",          // "": no wallpaper
    val wallpaperOpacity: Float = 0.35f,       // 0..1, how visible the wallpaper is
    val forceDarkMode: Boolean  = true,
    // Phase durations (minutes); defaults match the original 20-5-60-5 cycle
    val upskillingMinutes: Int  = 20,
    val eyeRest1Minutes: Int    = 5,
    val workMinutes: Int        = 60,
    val eyeRest2Minutes: Int    = 5,
    // Per-phase spoken reminder text, read aloud via TTS when that phase ends
    val upskillingReminder: String = "Time to start upskilling.",
    val eyeRest1Reminder: String   = "Start resting your eyes now.",
    val workReminder: String       = "Time to get to work.",
    val eyeRest2Reminder: String   = "Rest your eyes again before the next cycle.",
    // TTS voice/language/pace
    val ttsVoiceName: String   = "",   // raw Voice.name; "" = engine default
    val ttsLanguageTag: String = "",   // Locale.toLanguageTag(); "" = device default
    val ttsPace: Float = 1.0f,         // TextToSpeech.setSpeechRate(); UI range 0.5..2.0
    // Daily auto-start: when enabled, the cycle starts as a pill at this time each day
    val autoStartEnabled: Boolean = false,
    val autoStartHour: Int        = 6,   // 0..23
    val autoStartMinute: Int      = 10,  // 0..59
)

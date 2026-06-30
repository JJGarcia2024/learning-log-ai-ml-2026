package com.twinklingtreasure.timer.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

class SettingsRepository(private val context: Context) {

    companion object {
        private val KEY_VIBRATE        = booleanPreferencesKey("vibrate")
        private val KEY_WALLPAPER      = stringPreferencesKey("wallpaper_uri")
        private val KEY_OPACITY        = floatPreferencesKey("wallpaper_opacity")
        private val KEY_FORCE_DARK     = booleanPreferencesKey("force_dark")
        private val KEY_MIN_UPSKILLING = intPreferencesKey("min_upskilling")
        private val KEY_MIN_EYEREST1   = intPreferencesKey("min_eyerest1")
        private val KEY_MIN_WORK       = intPreferencesKey("min_work")
        private val KEY_MIN_EYEREST2   = intPreferencesKey("min_eyerest2")
        private val KEY_REMIND_UPSKILLING = stringPreferencesKey("remind_upskilling")
        private val KEY_REMIND_EYEREST1   = stringPreferencesKey("remind_eyerest1")
        private val KEY_REMIND_WORK       = stringPreferencesKey("remind_work")
        private val KEY_REMIND_EYEREST2   = stringPreferencesKey("remind_eyerest2")
        private val KEY_TTS_VOICE      = stringPreferencesKey("tts_voice_name")
        private val KEY_TTS_LANGUAGE   = stringPreferencesKey("tts_language_tag")
        private val KEY_TTS_PACE       = floatPreferencesKey("tts_pace")
        private val KEY_AUTOSTART_ON   = booleanPreferencesKey("autostart_on")
        private val KEY_AUTOSTART_HOUR = intPreferencesKey("autostart_hour")
        private val KEY_AUTOSTART_MIN  = intPreferencesKey("autostart_min")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { p ->
        AppSettings(
            vibrateEnabled   = p[KEY_VIBRATE]        ?: true,
            wallpaperUri     = p[KEY_WALLPAPER]      ?: "",
            wallpaperOpacity = p[KEY_OPACITY]        ?: 0.35f,
            forceDarkMode    = p[KEY_FORCE_DARK]     ?: true,
            upskillingMinutes = p[KEY_MIN_UPSKILLING] ?: 20,
            eyeRest1Minutes   = p[KEY_MIN_EYEREST1]   ?: 5,
            workMinutes       = p[KEY_MIN_WORK]       ?: 60,
            eyeRest2Minutes   = p[KEY_MIN_EYEREST2]   ?: 5,
            upskillingReminder = p[KEY_REMIND_UPSKILLING] ?: "Time to start upskilling.",
            eyeRest1Reminder   = p[KEY_REMIND_EYEREST1]   ?: "Start resting your eyes now.",
            workReminder       = p[KEY_REMIND_WORK]       ?: "Time to get to work.",
            eyeRest2Reminder   = p[KEY_REMIND_EYEREST2]   ?: "Rest your eyes again before the next cycle.",
            ttsVoiceName      = p[KEY_TTS_VOICE]      ?: "",
            ttsLanguageTag    = p[KEY_TTS_LANGUAGE]   ?: "",
            ttsPace           = p[KEY_TTS_PACE]       ?: 1.0f,
            autoStartEnabled  = p[KEY_AUTOSTART_ON]   ?: false,
            autoStartHour     = p[KEY_AUTOSTART_HOUR] ?: 6,
            autoStartMinute   = p[KEY_AUTOSTART_MIN]  ?: 10,
        )
    }

    suspend fun setVibrate(enabled: Boolean) =
        context.dataStore.edit { it[KEY_VIBRATE] = enabled }

    suspend fun setWallpaper(uri: String) =
        context.dataStore.edit { it[KEY_WALLPAPER] = uri }

    suspend fun setWallpaperOpacity(opacity: Float) =
        context.dataStore.edit { it[KEY_OPACITY] = opacity }

    suspend fun setForceDark(forced: Boolean) =
        context.dataStore.edit { it[KEY_FORCE_DARK] = forced }

    suspend fun setPhaseMinutes(phaseIndex: Int, minutes: Int) {
        val clamped = minutes.coerceIn(1, 180)
        context.dataStore.edit { prefs ->
            when (phaseIndex) {
                0 -> prefs[KEY_MIN_UPSKILLING] = clamped
                1 -> prefs[KEY_MIN_EYEREST1]   = clamped
                2 -> prefs[KEY_MIN_WORK]        = clamped
                3 -> prefs[KEY_MIN_EYEREST2]    = clamped
            }
        }
    }

    suspend fun setPhaseReminderText(phaseIndex: Int, text: String) {
        val trimmed = text.trim()
        context.dataStore.edit { prefs ->
            when (phaseIndex) {
                0 -> prefs[KEY_REMIND_UPSKILLING] = trimmed
                1 -> prefs[KEY_REMIND_EYEREST1]   = trimmed
                2 -> prefs[KEY_REMIND_WORK]       = trimmed
                3 -> prefs[KEY_REMIND_EYEREST2]   = trimmed
            }
        }
    }

    suspend fun setTtsVoice(voiceName: String) =
        context.dataStore.edit { it[KEY_TTS_VOICE] = voiceName }

    suspend fun setTtsLanguage(languageTag: String) =
        context.dataStore.edit { it[KEY_TTS_LANGUAGE] = languageTag }

    suspend fun setTtsPace(pace: Float) =
        context.dataStore.edit { it[KEY_TTS_PACE] = pace.coerceIn(0.5f, 2.0f) }

    suspend fun setAutoStartEnabled(enabled: Boolean) =
        context.dataStore.edit { it[KEY_AUTOSTART_ON] = enabled }

    suspend fun setAutoStartTime(hour: Int, minute: Int) {
        context.dataStore.edit {
            it[KEY_AUTOSTART_HOUR] = hour.coerceIn(0, 23)
            it[KEY_AUTOSTART_MIN]  = minute.coerceIn(0, 59)
        }
    }
}

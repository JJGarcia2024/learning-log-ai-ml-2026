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
        private val KEY_ALARM_URI      = stringPreferencesKey("alarm_uri")
        private val KEY_ALARM_NAME     = stringPreferencesKey("alarm_name")
        private val KEY_VIBRATE        = booleanPreferencesKey("vibrate")
        private val KEY_WALLPAPER      = stringPreferencesKey("wallpaper_uri")
        private val KEY_OPACITY        = floatPreferencesKey("wallpaper_opacity")
        private val KEY_FORCE_DARK     = booleanPreferencesKey("force_dark")
        private val KEY_MIN_UPSKILLING = intPreferencesKey("min_upskilling")
        private val KEY_MIN_EYEREST1   = intPreferencesKey("min_eyerest1")
        private val KEY_MIN_WORK       = intPreferencesKey("min_work")
        private val KEY_MIN_EYEREST2   = intPreferencesKey("min_eyerest2")
        private val KEY_AUTOSTART_ON   = booleanPreferencesKey("autostart_on")
        private val KEY_AUTOSTART_HOUR = intPreferencesKey("autostart_hour")
        private val KEY_AUTOSTART_MIN  = intPreferencesKey("autostart_min")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { p ->
        AppSettings(
            alarmSoundUri    = p[KEY_ALARM_URI]      ?: "default",
            alarmSoundName   = p[KEY_ALARM_NAME]     ?: "Default alarm",
            vibrateEnabled   = p[KEY_VIBRATE]        ?: true,
            wallpaperUri     = p[KEY_WALLPAPER]      ?: "",
            wallpaperOpacity = p[KEY_OPACITY]        ?: 0.35f,
            forceDarkMode    = p[KEY_FORCE_DARK]     ?: true,
            upskillingMinutes = p[KEY_MIN_UPSKILLING] ?: 20,
            eyeRest1Minutes   = p[KEY_MIN_EYEREST1]   ?: 5,
            workMinutes       = p[KEY_MIN_WORK]       ?: 60,
            eyeRest2Minutes   = p[KEY_MIN_EYEREST2]   ?: 5,
            autoStartEnabled  = p[KEY_AUTOSTART_ON]   ?: false,
            autoStartHour     = p[KEY_AUTOSTART_HOUR] ?: 6,
            autoStartMinute   = p[KEY_AUTOSTART_MIN]  ?: 10,
        )
    }

    suspend fun setAlarmSound(uri: String, name: String) {
        context.dataStore.edit {
            it[KEY_ALARM_URI]  = uri
            it[KEY_ALARM_NAME] = name
        }
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

    suspend fun setAutoStartEnabled(enabled: Boolean) =
        context.dataStore.edit { it[KEY_AUTOSTART_ON] = enabled }

    suspend fun setAutoStartTime(hour: Int, minute: Int) {
        context.dataStore.edit {
            it[KEY_AUTOSTART_HOUR] = hour.coerceIn(0, 23)
            it[KEY_AUTOSTART_MIN]  = minute.coerceIn(0, 59)
        }
    }
}

package com.twinklingtreasure.timer.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

class SettingsRepository(private val context: Context) {

    companion object {
        private val KEY_ALARM_URI   = stringPreferencesKey("alarm_uri")
        private val KEY_ALARM_NAME  = stringPreferencesKey("alarm_name")
        private val KEY_VIBRATE     = booleanPreferencesKey("vibrate")
        private val KEY_WALLPAPER   = stringPreferencesKey("wallpaper_uri")
        private val KEY_OPACITY     = floatPreferencesKey("wallpaper_opacity")
        private val KEY_FORCE_DARK  = booleanPreferencesKey("force_dark")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { p ->
        AppSettings(
            alarmSoundUri    = p[KEY_ALARM_URI]  ?: "default",
            alarmSoundName   = p[KEY_ALARM_NAME] ?: "Default alarm",
            vibrateEnabled   = p[KEY_VIBRATE]    ?: true,
            wallpaperUri     = p[KEY_WALLPAPER]  ?: "",
            wallpaperOpacity = p[KEY_OPACITY]    ?: 0.35f,
            forceDarkMode    = p[KEY_FORCE_DARK] ?: true,
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
}

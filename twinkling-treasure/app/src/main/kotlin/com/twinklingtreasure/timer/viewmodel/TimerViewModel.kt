package com.twinklingtreasure.timer.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.twinklingtreasure.timer.data.AppSettings
import com.twinklingtreasure.timer.data.SettingsRepository
import com.twinklingtreasure.timer.data.TimerCycle
import com.twinklingtreasure.timer.data.TimerState
import com.twinklingtreasure.timer.service.TimerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepo = SettingsRepository(application)

    private val _uiState = MutableStateFlow(TimerState())
    val uiState: StateFlow<TimerState> = _uiState.asStateFlow()

    private val _isInPip = MutableStateFlow(false)
    val isInPipMode: StateFlow<Boolean> = _isInPip.asStateFlow()

    val progressFraction: StateFlow<Float> = _uiState
        .map { s ->
            val total = TimerCycle.phases[s.currentPhaseIndex].durationSeconds.toFloat()
            if (total == 0f) 0f else 1f - (s.secondsRemaining / total)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0f)

    val settings: StateFlow<AppSettings> = settingsRepo.settings
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())

    private var service: TimerService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            service = (binder as TimerService.TimerBinder).getService()
            isBound = true
            viewModelScope.launch {
                service!!.timerState.collect { _uiState.value = it }
            }
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
            isBound = false
        }
    }

    fun bindService(context: Context) {
        context.bindService(Intent(context, TimerService::class.java), connection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService(context: Context) {
        if (isBound) { context.unbindService(connection); isBound = false }
    }

    fun setInPipMode(value: Boolean) { _isInPip.value = value }

    fun startTimer() = service?.start()
    fun pauseTimer() = service?.pause()
    fun skipPhase()  = service?.skipToNext()
    fun resetTimer() = service?.reset()

    fun setAlarmSound(uri: String, name: String) =
        viewModelScope.launch { settingsRepo.setAlarmSound(uri, name) }

    fun setVibrate(enabled: Boolean) =
        viewModelScope.launch { settingsRepo.setVibrate(enabled) }

    fun setWallpaper(uri: String) =
        viewModelScope.launch { settingsRepo.setWallpaper(uri) }

    fun setWallpaperOpacity(opacity: Float) =
        viewModelScope.launch { settingsRepo.setWallpaperOpacity(opacity) }

    fun setForceDark(forced: Boolean) =
        viewModelScope.launch { settingsRepo.setForceDark(forced) }

    fun testAlarm() {
        viewModelScope.launch(Dispatchers.Main) {
            val uriString = settings.value.alarmSoundUri
            if (uriString.isEmpty()) return@launch
            val uri = if (uriString == "default")
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            else Uri.parse(uriString)
            val mp = MediaPlayer()
            mp.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            try {
                mp.setDataSource(getApplication(), uri)
                mp.setOnPreparedListener { player ->
                    player.start()
                    viewModelScope.launch {
                        delay(3_000)
                        player.stop()
                        player.release()
                    }
                }
                mp.prepareAsync()
            } catch (_: Exception) {
                mp.release()
            }
        }
    }
}

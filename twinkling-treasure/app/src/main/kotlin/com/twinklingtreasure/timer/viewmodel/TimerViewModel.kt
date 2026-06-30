package com.twinklingtreasure.timer.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.speech.tts.Voice
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.twinklingtreasure.timer.alarm.AlarmScheduler
import com.twinklingtreasure.timer.data.AppSettings
import com.twinklingtreasure.timer.data.SettingsRepository
import com.twinklingtreasure.timer.data.TimerCycle
import com.twinklingtreasure.timer.data.TimerState
import com.twinklingtreasure.timer.service.TimerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepo = SettingsRepository(application)

    private val _uiState = MutableStateFlow(TimerState())
    val uiState: StateFlow<TimerState> = _uiState.asStateFlow()

    private val _isInPip = MutableStateFlow(false)
    val isInPipMode: StateFlow<Boolean> = _isInPip.asStateFlow()

    val settings: StateFlow<AppSettings> = settingsRepo.settings
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())

    val progressFraction: StateFlow<Float> = combine(_uiState, settings) { s, cfg ->
        val total = TimerCycle.phasesFor(cfg)[s.currentPhaseIndex].durationSeconds.toFloat()
        if (total == 0f) 0f else 1f - (s.secondsRemaining / total)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0f)

    private val _availableVoices = MutableStateFlow<List<Voice>>(emptyList())
    val availableVoices: StateFlow<List<Voice>> = _availableVoices.asStateFlow()

    private var service: TimerService? = null
    private var isBound = false

    init {
        // Keep the daily auto-start alarm in sync with settings.
        // Fires once when settings first load (re-arming after launch/reboot) and again
        // whenever the user toggles auto-start or changes the time.
        viewModelScope.launch {
            settingsRepo.settings
                .map { Triple(it.autoStartEnabled, it.autoStartHour, it.autoStartMinute) }
                .distinctUntilChanged()
                .collect {
                    withContext(Dispatchers.IO) { AlarmScheduler.scheduleDaily(getApplication()) }
                }
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val svc = (binder as TimerService.TimerBinder).getService()
            service = svc
            isBound = true
            viewModelScope.launch {
                svc.timerState.collect { _uiState.value = it }
            }
            viewModelScope.launch {
                svc.availableVoices.collect { _availableVoices.value = it }
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

    fun setInPipMode(value: Boolean) {
        _isInPip.value = value
        service?.setInPip(value)
    }

    fun setAppForeground(value: Boolean) { service?.setAppForeground(value) }

    fun startTimer() = service?.start()
    fun pauseTimer() = service?.pause()
    fun skipPhase()  = service?.skipToNext()
    fun resetTimer() = service?.reset()

    fun setVibrate(enabled: Boolean) =
        viewModelScope.launch { settingsRepo.setVibrate(enabled) }

    fun setWallpaper(uri: String) =
        viewModelScope.launch { settingsRepo.setWallpaper(uri) }

    fun setWallpaperOpacity(opacity: Float) =
        viewModelScope.launch { settingsRepo.setWallpaperOpacity(opacity) }

    fun setForceDark(forced: Boolean) =
        viewModelScope.launch { settingsRepo.setForceDark(forced) }

    fun setPhaseMinutes(phaseIndex: Int, minutes: Int) =
        viewModelScope.launch { settingsRepo.setPhaseMinutes(phaseIndex, minutes) }

    fun setPhaseReminderText(phaseIndex: Int, text: String) =
        viewModelScope.launch { settingsRepo.setPhaseReminderText(phaseIndex, text) }

    fun setTtsVoice(voiceName: String) =
        viewModelScope.launch { settingsRepo.setTtsVoice(voiceName) }

    fun setTtsLanguage(languageTag: String) =
        viewModelScope.launch { settingsRepo.setTtsLanguage(languageTag) }

    fun setTtsPace(pace: Float) =
        viewModelScope.launch { settingsRepo.setTtsPace(pace) }

    fun previewText(text: String) {
        service?.previewVoice(text)
    }

    fun setAutoStartEnabled(enabled: Boolean) =
        viewModelScope.launch { settingsRepo.setAutoStartEnabled(enabled) }

    fun setAutoStartTime(hour: Int, minute: Int) =
        viewModelScope.launch { settingsRepo.setAutoStartTime(hour, minute) }
}

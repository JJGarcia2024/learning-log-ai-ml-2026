package com.twinklingtreasure.timer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.twinklingtreasure.timer.MainActivity
import com.twinklingtreasure.timer.R
import com.twinklingtreasure.timer.data.AppSettings
import com.twinklingtreasure.timer.data.SettingsRepository
import com.twinklingtreasure.timer.data.TimerCycle
import com.twinklingtreasure.timer.data.TimerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TimerService : Service() {

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    private val binder      = TimerBinder()
    private val scope       = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var tickerJob: Job? = null

    private val _state = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _state.asStateFlow()

    private val settingsRepo by lazy { SettingsRepository(applicationContext) }
    private var currentSettings = AppSettings()
    @Volatile private var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createChannel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, buildNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIFICATION_ID, buildNotification())
        }
        scope.launch { settingsRepo.settings.collect { currentSettings = it } }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PAUSE -> pause()
            ACTION_SKIP  -> skipToNext()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        stopAlarm()
        scope.cancel()
        super.onDestroy()
    }

    fun start() {
        if (_state.value.isRunning) return
        _state.update { it.copy(isRunning = true) }
        tickerJob = scope.launch {
            while (isActive) {
                delay(1_000L)
                tick()
            }
        }
        updateNotification()
    }

    fun pause() {
        tickerJob?.cancel()
        stopAlarm()
        _state.update { it.copy(isRunning = false) }
        updateNotification()
    }

    fun skipToNext() {
        val wasRunning = _state.value.isRunning
        tickerJob?.cancel()
        stopAlarm()
        val next = (_state.value.currentPhaseIndex + 1) % TimerCycle.phases.size
        _state.update {
            it.copy(
                currentPhaseIndex = next,
                secondsRemaining  = TimerCycle.phases[next].durationSeconds,
                isRunning         = false,
            )
        }
        if (wasRunning) start() else updateNotification()
    }

    fun reset() {
        tickerJob?.cancel()
        stopAlarm()
        _state.value = TimerState()
        updateNotification()
    }

    private fun tick() {
        val s = _state.value
        if (s.secondsRemaining > 0) {
            _state.update { it.copy(secondsRemaining = it.secondsRemaining - 1) }
        } else {
            triggerPhaseEnd()
            val next = (s.currentPhaseIndex + 1) % TimerCycle.phases.size
            _state.update {
                it.copy(
                    currentPhaseIndex = next,
                    secondsRemaining  = TimerCycle.phases[next].durationSeconds,
                )
            }
        }
        updateNotification()
    }

    private fun triggerPhaseEnd() {
        if (currentSettings.vibrateEnabled) triggerVibration()
        val uri = currentSettings.alarmSoundUri
        if (uri.isNotEmpty()) playAlarm(uri)
    }

    private fun triggerVibration() {
        val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        val pattern = longArrayOf(0, 400, 150, 400, 150, 400)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }

    private fun playAlarm(uriString: String) {
        scope.launch(Dispatchers.Main) {
            stopAlarm()
            val uri = if (uriString == "default") {
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            } else {
                Uri.parse(uriString)
            }
            val mp = MediaPlayer()
            mp.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            try {
                mp.setDataSource(applicationContext, uri)
                mp.setOnPreparedListener { it.start() }
                mp.setOnCompletionListener { it.release(); if (mediaPlayer === it) mediaPlayer = null }
                mp.prepareAsync()
                mediaPlayer = mp
            } catch (_: Exception) {
                mp.release()
            }
        }
    }

    private fun stopAlarm() {
        mediaPlayer?.run {
            try { if (isPlaying) stop() } catch (_: Exception) {}
            release()
        }
        mediaPlayer = null
    }

    private fun buildNotification(): Notification {
        val s     = _state.value
        val phase = TimerCycle.phases[s.currentPhaseIndex]
        val mm    = s.secondsRemaining / 60
        val ss    = s.secondsRemaining % 60

        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_SINGLE_TOP },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val pauseIntent = PendingIntent.getService(
            this, 1,
            Intent(this, TimerService::class.java).apply { action = ACTION_PAUSE },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val skipIntent = PendingIntent.getService(
            this, 2,
            Intent(this, TimerService::class.java).apply { action = ACTION_SKIP },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("${phase.emoji}  ${phase.name}")
            .setContentText("%02d:%02d".format(mm, ss))
            .setContentIntent(openIntent)
            .setOngoing(true)
            .setSilent(true)
            .addAction(android.R.drawable.ic_media_pause, if (s.isRunning) "Pause" else "Resume", pauseIntent)
            .addAction(android.R.drawable.ic_media_next, "Skip", skipIntent)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    private fun updateNotification() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, buildNotification())
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Cycle Timer", NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = "Ongoing productivity cycle timer"
                setShowBadge(false)
            }
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID      = "twinkling_timer_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_PAUSE    = "com.twinklingtreasure.ACTION_PAUSE"
        const val ACTION_SKIP     = "com.twinklingtreasure.ACTION_SKIP"
    }
}

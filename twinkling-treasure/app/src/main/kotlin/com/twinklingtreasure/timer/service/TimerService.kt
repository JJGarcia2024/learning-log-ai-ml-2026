package com.twinklingtreasure.timer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.twinklingtreasure.timer.MainActivity
import com.twinklingtreasure.timer.R
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

    override fun onBind(intent: Intent): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PAUSE -> pause()
            ACTION_SKIP  -> skipToNext()
        }
        return START_STICKY
    }

    override fun onDestroy() {
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
        _state.update { it.copy(isRunning = false) }
        updateNotification()
    }

    fun skipToNext() {
        val wasRunning = _state.value.isRunning
        tickerJob?.cancel()
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
        _state.value = TimerState()
        updateNotification()
    }

    private fun tick() {
        val s = _state.value
        if (s.secondsRemaining > 0) {
            _state.update { it.copy(secondsRemaining = it.secondsRemaining - 1) }
        } else {
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

    private fun buildNotification(): Notification {
        val s     = _state.value
        val phase = TimerCycle.phases[s.currentPhaseIndex]
        val mm    = s.secondsRemaining / 60
        val ss    = s.secondsRemaining % 60
        val time  = "%02d:%02d".format(mm, ss)

        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
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

        val pauseLabel = if (s.isRunning) "Pause" else "Resume"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("${phase.emoji}  ${phase.name}")
            .setContentText(time)
            .setContentIntent(openIntent)
            .setOngoing(true)
            .setSilent(true)
            .addAction(android.R.drawable.ic_media_pause, pauseLabel, pauseIntent)
            .addAction(android.R.drawable.ic_media_next,  "Skip",     skipIntent)
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
                CHANNEL_ID,
                "Cycle Timer",
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description  = "Ongoing productivity cycle timer"
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

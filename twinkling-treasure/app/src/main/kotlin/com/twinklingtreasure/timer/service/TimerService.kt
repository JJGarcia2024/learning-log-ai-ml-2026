package com.twinklingtreasure.timer.service

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.twinklingtreasure.timer.MainActivity
import com.twinklingtreasure.timer.R
import com.twinklingtreasure.timer.data.AppSettings
import com.twinklingtreasure.timer.data.SettingsRepository
import com.twinklingtreasure.timer.data.TimerCycle
import com.twinklingtreasure.timer.data.TimerState
import com.twinklingtreasure.timer.util.formatTime
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
import kotlinx.coroutines.withContext

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

    // ── Overlay (Live Alert-style pill) ────────────────────────────
    private var wm: WindowManager? = null
    private var overlayRoot: LinearLayout? = null
    private var overlayEmoji: TextView? = null
    private var overlayTime: TextView? = null
    // Default false: if the service restarts after process kill, no activity is present
    private var isAppForeground = false
    private var isInPip = false

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
        restoreState()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // Use commit() (synchronous) so the write completes before the process is killed
        val s = _state.value
        prefs().edit()
            .putInt("phase_index", s.currentPhaseIndex)
            .putInt("seconds_remaining", s.secondsRemaining)
            .putBoolean("is_running", s.isRunning)
            .putLong("saved_at_ms", System.currentTimeMillis())
            .commit()
        // On OEM devices (e.g. OnePlus) the foreground service can be killed despite
        // stopWithTask=false. Schedule a 1-second alarm so the pill reappears quickly.
        if (s.isRunning) scheduleRestart()
    }

    private fun scheduleRestart() {
        val pi = PendingIntent.getService(
            this, REQUEST_RESTART,
            Intent(this, TimerService::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val at = SystemClock.elapsedRealtime() + 1_000L
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && am.canScheduleExactAlarms() ->
                am.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, at, pi)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
                am.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, at, pi)
            else ->
                am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, at, pi)
        }
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
        destroyOverlay()
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
                if (overlayRoot != null) withContext(Dispatchers.Main) { refreshOverlay() }
            }
        }
        scope.launch(Dispatchers.Main) { syncOverlay() }
        updateNotification()
    }

    fun pause() {
        tickerJob?.cancel()
        stopAlarm()
        _state.update { it.copy(isRunning = false) }
        scope.launch(Dispatchers.Main) { syncOverlay() }
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
                secondsRemaining  = durationFor(next),
                isRunning         = false,
            )
        }
        if (wasRunning) start() else { scope.launch(Dispatchers.Main) { syncOverlay() }; updateNotification() }
    }

    fun reset() {
        tickerJob?.cancel()
        stopAlarm()
        _state.value = TimerState(secondsRemaining = durationFor(0))
        clearPersistedState()
        scope.launch(Dispatchers.Main) { syncOverlay() }
        updateNotification()
    }

    // ── Overlay public API (called by ViewModel) ───────────────────
    fun setAppForeground(value: Boolean) {
        isAppForeground = value
        scope.launch(Dispatchers.Main) { syncOverlay() }
    }

    fun setInPip(value: Boolean) {
        isInPip = value
        scope.launch(Dispatchers.Main) { syncOverlay() }
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
                    secondsRemaining  = durationFor(next),
                )
            }
        }
        persistState()
        updateNotification()
    }

    // ── State persistence (survives swipe-from-recents process kill) ─

    private fun prefs() = getSharedPreferences("timer_state", Context.MODE_PRIVATE)

    private fun persistState() {
        val s = _state.value
        prefs().edit()
            .putInt("phase_index", s.currentPhaseIndex)
            .putInt("seconds_remaining", s.secondsRemaining)
            .putBoolean("is_running", s.isRunning)
            .putLong("saved_at_ms", System.currentTimeMillis())
            .apply()
    }

    private fun clearPersistedState() {
        prefs().edit().clear().apply()
    }

    private fun restoreState() {
        val p = prefs()
        if (!p.getBoolean("is_running", false)) return
        val phaseIndex   = p.getInt("phase_index", 0)
        val savedSeconds = p.getInt("seconds_remaining", 0)
        val savedAt      = p.getLong("saved_at_ms", 0L)
        val elapsed      = ((System.currentTimeMillis() - savedAt) / 1000L).toInt().coerceAtLeast(0)
        val adjusted     = savedSeconds - elapsed
        val (finalIndex, finalSeconds) = if (adjusted > 0) {
            phaseIndex to adjusted
        } else {
            // Phase has already passed; advance to next
            val next = (phaseIndex + 1) % TimerCycle.phases.size
            next to durationFor(next)
        }
        _state.value = TimerState(currentPhaseIndex = finalIndex, secondsRemaining = finalSeconds, isRunning = false)
        start()
    }

    /** Returns the configured duration for a phase index, using current settings. */
    private fun durationFor(phaseIndex: Int): Int = when (phaseIndex) {
        0 -> currentSettings.upskillingMinutes * 60
        1 -> currentSettings.eyeRest1Minutes   * 60
        2 -> currentSettings.workMinutes       * 60
        3 -> currentSettings.eyeRest2Minutes   * 60
        else -> TimerCycle.phases[phaseIndex].durationSeconds
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

    // ── Overlay helpers (must run on Main thread) ──────────────────

    private fun syncOverlay() {
        val shouldShow = _state.value.isRunning && !isAppForeground && !isInPip &&
                Settings.canDrawOverlays(this)
        if (shouldShow) {
            if (overlayRoot == null) buildOverlay()
            refreshOverlay()
        } else {
            destroyOverlay()
        }
    }

    private fun buildOverlay() {
        val density = resources.displayMetrics.density

        fun Int.px() = (this * density).toInt()
        fun Float.px() = this * density

        val pill = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = GradientDrawable().apply {
                setColor(0xF00A1628.toInt())
                cornerRadius = 100f.px()
            }
            setPadding(18.px(), 10.px(), 18.px(), 10.px())
            elevation = 10f.px()
            setOnClickListener {
                startActivity(
                    Intent(applicationContext, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                )
            }
        }

        val phase = TimerCycle.phasesFor(currentSettings)[_state.value.currentPhaseIndex]

        val emojiTv = TextView(this).apply {
            text = phase.emoji
            textSize = 16f
            setPadding(0, 0, 10.px(), 0)
        }
        val timeTv = TextView(this).apply {
            text = formatTime(_state.value.secondsRemaining)
            textSize = 18f
            setTypeface(Typeface.DEFAULT_BOLD)
            setTextColor(Color.parseColor("#FFD700"))
        }

        pill.addView(emojiTv)
        pill.addView(timeTv)
        overlayEmoji = emojiTv
        overlayTime  = timeTv
        overlayRoot  = pill

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = 8
        }

        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        this.wm = wm
        wm.addView(pill, params)
    }

    private fun refreshOverlay() {
        val s     = _state.value
        val phase = TimerCycle.phasesFor(currentSettings)[s.currentPhaseIndex]
        overlayEmoji?.text = phase.emoji
        overlayTime?.text  = formatTime(s.secondsRemaining)
    }

    private fun destroyOverlay() {
        overlayRoot?.let { v ->
            try { wm?.removeView(v) } catch (_: Exception) {}
        }
        overlayRoot  = null
        overlayEmoji = null
        overlayTime  = null
        wm           = null
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
        private const val REQUEST_RESTART = 9001
    }
}

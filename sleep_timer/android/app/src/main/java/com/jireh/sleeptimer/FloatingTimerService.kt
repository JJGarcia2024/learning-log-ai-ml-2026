package com.jireh.sleeptimer

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
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.abs

/**
 * A draggable floating overlay that shows the live countdown.
 *
 * Unlike Android's Picture-in-Picture (of which the system allows only ONE at a
 * time), a TYPE_APPLICATION_OVERLAY window is independent of the PiP subsystem,
 * so this timer happily coexists on top of another app's PiP (e.g. YouTube).
 */
class FloatingTimerService : Service() {

    companion object {
        const val ACTION_STOP = "com.jireh.sleeptimer.STOP_OVERLAY"
        private const val CHANNEL_ID = "sleep_float"
        private const val NOTIF_ID = 4201
    }

    private lateinit var wm: WindowManager
    private lateinit var prefs: SleepPrefs
    private var root: View? = null
    private lateinit var params: WindowManager.LayoutParams

    private lateinit var titleText: TextView
    private lateinit var timeText: TextView
    private lateinit var subText: TextView

    private val handler = Handler(Looper.getMainLooper())
    private val tick = object : Runnable {
        override fun run() {
            update()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        prefs = SleepPrefs(this)
        startAsForeground()
        addOverlay()
        handler.post(tick)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }
        return START_STICKY
    }

    /* ── Foreground notification (required to keep the overlay alive) ── */
    private fun startAsForeground() {
        val nm = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Floating timer", NotificationManager.IMPORTANCE_LOW
            ).apply { setShowBadge(false) }
            nm.createNotificationChannel(channel)
        }

        val openPi = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notif: Notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Sleep Timer floating")
            .setContentText("Tap to open the app")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(openPi)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIF_ID, notif, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIF_ID, notif)
        }
    }

    /* ── Build and attach the floating view ── */
    private fun addOverlay() {
        val density = resources.displayMetrics.density
        fun dp(v: Float) = (v * density).toInt()

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18f), dp(14f), dp(18f), dp(16f))
            background = GradientDrawable().apply {
                cornerRadius = dp(20f).toFloat()
                setColor(0xF20D1B3E.toInt())          // translucent deep blue
                setStroke(dp(1.5f), 0xFFFFD740.toInt()) // gold border
            }
        }

        // Title row: "SLEEP TIMER"  +  close ×
        val titleRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        titleText = TextView(this).apply {
            text = "SLEEP TIMER"
            setTextColor(0xFF82B1FF.toInt())
            textSize = 10f
            letterSpacing = 0.15f
        }
        val closeBtn = TextView(this).apply {
            text = "✕"
            setTextColor(0xFF8FA3D0.toInt())
            textSize = 14f
            setPadding(dp(10f), 0, 0, 0)
            setOnClickListener { stopSelf() }
        }
        titleRow.addView(titleText,
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        titleRow.addView(closeBtn)

        timeText = TextView(this).apply {
            text = "--:--:--"
            setTextColor(Color.WHITE)
            textSize = 30f
            setPadding(0, dp(2f), 0, dp(2f))
        }
        subText = TextView(this).apply {
            text = ""
            setTextColor(0xFFFFD740.toInt())
            textSize = 11f
        }

        container.addView(titleRow)
        container.addView(timeText)
        container.addView(subText)

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = dp(20f)
            y = dp(120f)
        }

        attachDragAndTap(container)

        root = container
        wm.addView(container, params)
    }

    /* ── Drag to move, tap to open the app ── */
    private fun attachDragAndTap(view: View) {
        var downX = 0f; var downY = 0f
        var startX = 0; var startY = 0
        var dragged = false
        val touchSlop = 12 * resources.displayMetrics.density

        view.setOnTouchListener { _, e ->
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = e.rawX; downY = e.rawY
                    startX = params.x; startY = params.y
                    dragged = false
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (e.rawX - downX); val dy = (e.rawY - downY)
                    if (abs(dx) > touchSlop || abs(dy) > touchSlop) dragged = true
                    params.x = startX + dx.toInt()
                    params.y = startY + dy.toInt()
                    root?.let { wm.updateViewLayout(it, params) }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (!dragged) {
                        startActivity(
                            Intent(this, MainActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                    true
                }
                else -> false
            }
        }
    }

    /* ── Refresh the countdown text every second ── */
    private fun update() {
        val snap = TimerEngine.snapshot(
            prefs.sleepHour, prefs.sleepMinute, prefs.wakeHour, prefs.wakeMinute
        )
        val secs = if (snap.active) snap.remaining else snap.untilStart
        val (h, m, s) = TimerEngine.hms(secs)
        timeText.text = "%02d:%02d:%02d".format(h, m, s)
        titleText.text = if (snap.active) "SLEEP TIMER" else "STARTS IN"

        val ampm = if (snap.active) {
            fmt12(prefs.wakeHour, prefs.wakeMinute).let { "Wake at $it" }
        } else {
            fmt12(prefs.sleepHour, prefs.sleepMinute).let { "Starts $it" }
        }
        subText.text = ampm
    }

    private fun fmt12(h: Int, m: Int): String {
        val ap = if (h >= 12) "PM" else "AM"
        val hh = h % 12
        return "%d:%02d %s".format(if (hh == 0) 12 else hh, m, ap)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(tick)
        root?.let { runCatching { wm.removeView(it) } }
        root = null
    }
}

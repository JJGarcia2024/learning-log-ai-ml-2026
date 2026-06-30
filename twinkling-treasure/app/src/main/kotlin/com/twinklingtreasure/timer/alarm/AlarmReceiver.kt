package com.twinklingtreasure.timer.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.twinklingtreasure.timer.service.TimerService

/**
 * Fires at the user's chosen time. Starts the timer SERVICE directly (no activity launch),
 * so the cycle begins in the background and the Live-Alert pill appears — NOT Picture-in-Picture.
 */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val svc = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_AUTO_START
        }
        // A firing alarm grants a short allowlist window, so starting a foreground service
        // from the background here is permitted.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(svc)
        } else {
            context.startService(svc)
        }

        // Re-arm for the next day.
        AlarmScheduler.scheduleDaily(context)
    }
}

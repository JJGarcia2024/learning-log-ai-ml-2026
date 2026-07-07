package com.jireh.sleeptimer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings

/**
 * Fires at the configured sleep time and floats the countdown over whatever
 * you're doing, then re-schedules tomorrow's alarm.
 *
 * Preference order:
 *  1. Overlay window (FloatingTimerService) — coexists with another app's PiP.
 *  2. Fallback: launch MainActivity and enter native Picture-in-Picture.
 */
class SleepAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (Settings.canDrawOverlays(context)) {
            context.startForegroundService(
                Intent(context, FloatingTimerService::class.java)
            )
        } else {
            val launch = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(MainActivity.EXTRA_AUTO_PIP, true)
            }
            context.startActivity(launch)
        }

        // Re-arm for the next day.
        SleepAlarmScheduler.schedule(context)
    }
}

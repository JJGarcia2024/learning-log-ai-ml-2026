package com.jireh.sleeptimer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Fires at the configured sleep time. Launches MainActivity which immediately
 * enters Picture-in-Picture so the countdown floats over whatever you're doing.
 * Then re-schedules tomorrow's alarm.
 */
class SleepAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val launch = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(MainActivity.EXTRA_AUTO_PIP, true)
        }
        context.startActivity(launch)

        // Re-arm for the next day.
        SleepAlarmScheduler.schedule(context)
    }
}

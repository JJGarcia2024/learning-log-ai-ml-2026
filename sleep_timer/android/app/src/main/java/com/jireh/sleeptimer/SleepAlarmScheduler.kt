package com.jireh.sleeptimer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

/** Schedules a daily exact alarm at the configured sleep time. */
object SleepAlarmScheduler {

    private const val REQUEST_CODE = 9021

    fun schedule(context: Context) {
        val prefs = SleepPrefs(context)
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val next = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, prefs.sleepHour)
            set(Calendar.MINUTE, prefs.sleepMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val pi = PendingIntent.getBroadcast(
            context, REQUEST_CODE,
            Intent(context, SleepAlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Exact alarm if allowed, otherwise fall back to an inexact one.
        val canExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            am.canScheduleExactAlarms()
        } else true

        if (canExact) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next.timeInMillis, pi)
        } else {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next.timeInMillis, pi)
        }
    }
}

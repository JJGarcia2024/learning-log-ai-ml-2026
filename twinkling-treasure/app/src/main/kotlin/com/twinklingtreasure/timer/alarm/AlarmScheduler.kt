package com.twinklingtreasure.timer.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

/** Schedules a daily exact alarm that opens the app in PiP and starts the cycle. */
object AlarmScheduler {

    const val HOUR   = 6
    const val MINUTE = 10

    fun scheduleDaily(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // On Android 12+ exact alarms may require permission; bail quietly if not allowed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            return
        }

        val triggerAt = nextTriggerMillis()
        val showIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, com.twinklingtreasure.timer.MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val info = AlarmManager.AlarmClockInfo(triggerAt, showIntent)
        am.setAlarmClock(info, alarmPendingIntent(context))
    }

    fun cancel(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(alarmPendingIntent(context))
    }

    private fun alarmPendingIntent(context: Context): PendingIntent =
        PendingIntent.getBroadcast(
            context, REQUEST_CODE,
            Intent(context, AlarmReceiver::class.java).apply { action = ACTION_FIRE },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    private fun nextTriggerMillis(): Long {
        val now = Calendar.getInstance()
        val next = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, HOUR)
            set(Calendar.MINUTE, MINUTE)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (next.timeInMillis <= now.timeInMillis) {
            next.add(Calendar.DAY_OF_YEAR, 1)
        }
        return next.timeInMillis
    }

    const val ACTION_FIRE  = "com.twinklingtreasure.ALARM_FIRE"
    private const val REQUEST_CODE = 7610
}

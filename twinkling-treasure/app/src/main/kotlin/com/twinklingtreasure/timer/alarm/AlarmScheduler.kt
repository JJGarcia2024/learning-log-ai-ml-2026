package com.twinklingtreasure.timer.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.twinklingtreasure.timer.data.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Calendar

/**
 * Schedules a daily exact alarm that starts the cycle as a floating pill (no PiP, no app UI).
 * The trigger time and on/off state come from the user's saved settings.
 */
object AlarmScheduler {

    /** Reads settings and (re)arms or cancels the daily alarm accordingly. */
    fun scheduleDaily(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val settings = runBlocking { SettingsRepository(context).settings.first() }

        // Auto-start disabled → ensure nothing is pending and bail.
        if (!settings.autoStartEnabled) {
            am.cancel(alarmPendingIntent(context))
            return
        }

        // On Android 12+ exact alarms may require permission; bail quietly if not allowed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            return
        }

        val triggerAt = nextTriggerMillis(settings.autoStartHour, settings.autoStartMinute)
        // showIntent is only what opens when the user taps the status-bar alarm chip.
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

    private fun nextTriggerMillis(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val next = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
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

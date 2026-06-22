package com.twinklingtreasure.timer.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.twinklingtreasure.timer.MainActivity
import com.twinklingtreasure.timer.R

/** Fires at 6:10 AM: opens the app (PiP + auto-start) and reschedules for tomorrow. */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val launch = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(MainActivity.EXTRA_AUTO_START, true)
        }

        // Try a direct foreground launch first.
        try {
            context.startActivity(launch)
        } catch (_: Exception) {
            // ignore — fall back to the full-screen-intent notification below
        }

        // Full-screen intent notification guarantees launch even from the background.
        val fullScreenPi = PendingIntent.getActivity(
            context, 100, launch,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        createChannel(context)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Spacesmith's Timer")
            .setContentText("Starting your 6:10 AM cycle")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setFullScreenIntent(fullScreenPi, true)
            .build()
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(NOTIFICATION_ID, notification)

        // Re-arm for the next day.
        AlarmScheduler.scheduleDaily(context)
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Daily Auto-Start", NotificationManager.IMPORTANCE_HIGH,
            ).apply { description = "Opens the timer automatically at 6:10 AM" }
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID      = "twinkling_autostart_channel"
        private const val NOTIFICATION_ID = 2010
    }
}

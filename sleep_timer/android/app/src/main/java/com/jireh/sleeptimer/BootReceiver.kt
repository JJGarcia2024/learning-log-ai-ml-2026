package com.jireh.sleeptimer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Re-arms the daily sleep alarm after the device reboots. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            SleepAlarmScheduler.schedule(context)
        }
    }
}

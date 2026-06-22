package com.twinklingtreasure.timer.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Re-arms the daily 6:10 AM alarm after a device reboot. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            AlarmScheduler.scheduleDaily(context)
        }
    }
}

package com.jireh.sleeptimer

import java.util.Calendar

/** Pure time math for the sleep window. All durations are in seconds. */
object TimerEngine {

    data class Snapshot(
        val active: Boolean,        // true if we are currently inside the sleep window
        val remaining: Int,         // seconds left until wake (when active)
        val untilStart: Int,        // seconds until the window starts (when not active)
        val total: Int,             // total length of the sleep window in seconds
        val progress: Float,        // 0f..1f, how far through the window we are
    )

    private fun mins(h: Int, m: Int) = h * 60 + m

    fun windowSeconds(sleepH: Int, sleepM: Int, wakeH: Int, wakeM: Int): Int {
        var dur = mins(wakeH, wakeM) - mins(sleepH, sleepM)
        if (dur <= 0) dur += 1440          // crosses midnight
        return dur * 60
    }

    fun snapshot(
        sleepH: Int, sleepM: Int, wakeH: Int, wakeM: Int,
        now: Calendar = Calendar.getInstance()
    ): Snapshot {
        val nowSec = now.get(Calendar.HOUR_OF_DAY) * 3600 +
            now.get(Calendar.MINUTE) * 60 +
            now.get(Calendar.SECOND)

        val sleepSec = mins(sleepH, sleepM) * 60
        val wakeSec  = mins(wakeH, wakeM) * 60
        val total    = windowSeconds(sleepH, sleepM, wakeH, wakeM)
        val crosses  = sleepSec > wakeSec

        val inWindow = if (crosses) nowSec >= sleepSec || nowSec < wakeSec
                       else nowSec in sleepSec until wakeSec

        return if (inWindow) {
            var remaining = wakeSec - nowSec
            if (remaining < 0) remaining += 86400
            val progress = (1f - remaining.toFloat() / total).coerceIn(0f, 1f)
            Snapshot(true, remaining, 0, total, progress)
        } else {
            var untilStart = sleepSec - nowSec
            if (untilStart < 0) untilStart += 86400
            Snapshot(false, 0, untilStart, total, 0f)
        }
    }

    /** Formats seconds as HH:MM:SS. */
    fun hms(totalSecs: Int): Triple<Int, Int, Int> {
        val h = totalSecs / 3600
        val m = (totalSecs % 3600) / 60
        val s = totalSecs % 60
        return Triple(h, m, s)
    }
}

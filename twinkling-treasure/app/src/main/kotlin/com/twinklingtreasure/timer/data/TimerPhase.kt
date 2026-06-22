package com.twinklingtreasure.timer.data

import androidx.compose.ui.graphics.Color

data class TimerPhase(
    val id: Int,
    val name: String,
    val emoji: String,
    val durationSeconds: Int,
    val accentLight: Color,
    val accentDark: Color,
    val description: String,
)

object TimerCycle {
    /** Returns the phase list with durations overridden by the user's saved settings. */
    fun phasesFor(settings: AppSettings) = listOf(
        phases[0].copy(durationSeconds = settings.upskillingMinutes * 60),
        phases[1].copy(durationSeconds = settings.eyeRest1Minutes   * 60),
        phases[2].copy(durationSeconds = settings.workMinutes       * 60),
        phases[3].copy(durationSeconds = settings.eyeRest2Minutes   * 60),
    )

    val phases = listOf(
        TimerPhase(
            id = 0,
            name = "Upskilling",
            emoji = "📚",
            durationSeconds = 20 * 60,
            accentLight = Color(0xFFF9A825),
            accentDark  = Color(0xFFFFD700),
            description = "Learning & growth",
        ),
        TimerPhase(
            id = 1,
            name = "Eye Rest",
            emoji = "😌",
            durationSeconds = 5 * 60,
            accentLight = Color(0xFF0D47A1),
            accentDark  = Color(0xFF1565C0),
            description = "Close eyes — music or ASMR",
        ),
        TimerPhase(
            id = 2,
            name = "Work",
            emoji = "⚡",
            durationSeconds = 60 * 60,
            accentLight = Color(0xFFF9A825),
            accentDark  = Color(0xFFFFD700),
            description = "Deep focus session",
        ),
        TimerPhase(
            id = 3,
            name = "Eye Rest",
            emoji = "😌",
            durationSeconds = 5 * 60,
            accentLight = Color(0xFF0D47A1),
            accentDark  = Color(0xFF1565C0),
            description = "Close eyes — music or ASMR",
        ),
    )
}

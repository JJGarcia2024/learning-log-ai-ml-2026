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
    val phases = listOf(
        TimerPhase(
            id = 0,
            name = "Leisure",
            emoji = "🎬",
            durationSeconds = 5 * 60,
            accentLight = Color(0xFF1976D2),
            accentDark  = Color(0xFF42A5F5),
            description = "Dopamine Menu — streaming & fun",
        ),
        TimerPhase(
            id = 1,
            name = "Work",
            emoji = "⚡",
            durationSeconds = 52 * 60,
            accentLight = Color(0xFFF9A825),
            accentDark  = Color(0xFFFFD700),
            description = "Deep focus session",
        ),
        TimerPhase(
            id = 2,
            name = "Eye Rest",
            emoji = "😌",
            durationSeconds = 6 * 60,
            accentLight = Color(0xFF0D47A1),
            accentDark  = Color(0xFF1565C0),
            description = "Close eyes — music or ASMR",
        ),
        TimerPhase(
            id = 3,
            name = "Upskilling",
            emoji = "📚",
            durationSeconds = 17 * 60,
            accentLight = Color(0xFFF9A825),
            accentDark  = Color(0xFFFFD700),
            description = "Learning & growth",
        ),
        TimerPhase(
            id = 4,
            name = "Eye Rest",
            emoji = "😌",
            durationSeconds = 6 * 60,
            accentLight = Color(0xFF0D47A1),
            accentDark  = Color(0xFF1565C0),
            description = "Close eyes — music or ASMR",
        ),
    )
}

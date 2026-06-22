package com.twinklingtreasure.timer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twinklingtreasure.timer.data.TimerCycle
import com.twinklingtreasure.timer.util.formatTime

@Composable
fun PipContent(
    currentPhaseIndex: Int,
    secondsRemaining: Int,
) {
    val dark   = isSystemInDarkTheme()
    val phase  = TimerCycle.phases[currentPhaseIndex]
    val accent = if (dark) phase.accentDark else phase.accentLight

    Column(
        verticalArrangement   = Arrangement.Center,
        horizontalAlignment   = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(4.dp),
    ) {
        Text(text = phase.emoji, fontSize = 18.sp)
        Spacer(Modifier.height(2.dp))
        Text(
            text       = formatTime(secondsRemaining),
            color      = accent,
            fontSize   = 26.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text     = phase.name.uppercase(),
            style    = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            color    = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            fontSize = 9.sp,
        )
    }
}

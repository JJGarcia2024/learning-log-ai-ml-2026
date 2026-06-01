package com.twinklingtreasure.timer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twinklingtreasure.timer.data.TimerCycle
import com.twinklingtreasure.timer.util.formatTime

@Composable
fun PipContent(
    currentPhaseIndex: Int,
    secondsRemaining: Int,
) {
    val dark  = isSystemInDarkTheme()
    val phase = TimerCycle.phases[currentPhaseIndex]
    val accent = if (dark) phase.accentDark else phase.accentLight

    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 12.dp),
    ) {
        Text(text = phase.emoji, fontSize = 28.sp)
        Spacer(Modifier.width(8.dp))
        Text(
            text  = formatTime(secondsRemaining),
            style = MaterialTheme.typography.headlineLarge,
            color = accent,
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text  = phase.name,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        )
    }
}

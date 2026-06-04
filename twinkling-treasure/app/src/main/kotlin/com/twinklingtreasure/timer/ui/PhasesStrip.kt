package com.twinklingtreasure.timer.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twinklingtreasure.timer.data.TimerCycle
import com.twinklingtreasure.timer.util.formatTime

@Composable
fun PhasesStrip(
    currentPhaseIndex: Int,
    secondsRemaining: Int,
    modifier: Modifier = Modifier,
) {
    val dark = isSystemInDarkTheme()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TimerCycle.phases.forEachIndexed { index, phase ->
            val isCurrent  = index == currentPhaseIndex
            val isComplete = index < currentPhaseIndex
            val accent     = if (dark) phase.accentDark else phase.accentLight

            val scale by animateFloatAsState(
                targetValue   = if (isCurrent) 1.12f else 1f,
                animationSpec = tween(300),
                label         = "chipScale$index",
            )
            val bgAlpha by animateFloatAsState(
                targetValue   = if (isCurrent) 0.18f else 0.06f,
                animationSpec = tween(300),
                label         = "chipBg$index",
            )
            val labelColor by animateColorAsState(
                targetValue   = if (isCurrent) accent else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                animationSpec = tween(300),
                label         = "labelColor$index",
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .scale(scale)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = bgAlpha))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
            ) {
                Text(
                    text     = if (isComplete) "✓" else phase.emoji,
                    fontSize = 18.sp,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text  = phase.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = labelColor,
                )
                if (isCurrent) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text  = formatTime(secondsRemaining),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = accent,
                    )
                    Spacer(Modifier.height(3.dp))
                    Box(
                        Modifier
                            .width(20.dp)
                            .height(2.dp)
                            .background(accent, RoundedCornerShape(1.dp))
                    )
                }
            }
        }
    }
}

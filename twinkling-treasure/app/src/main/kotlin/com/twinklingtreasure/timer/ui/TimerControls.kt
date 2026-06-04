package com.twinklingtreasure.timer.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TimerControls(
    isRunning: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onSkip: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onReset) {
            Icon(
                imageVector        = Icons.Rounded.Replay,
                contentDescription = "Reset",
                tint               = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier           = Modifier.size(28.dp),
            )
        }

        FloatingActionButton(
            onClick            = if (isRunning) onPause else onStart,
            containerColor     = MaterialTheme.colorScheme.primary,
            contentColor       = MaterialTheme.colorScheme.onPrimary,
            elevation          = FloatingActionButtonDefaults.elevation(8.dp),
            modifier           = Modifier.size(72.dp),
        ) {
            Crossfade(
                targetState   = isRunning,
                animationSpec = tween(200),
                label         = "playPause",
            ) { running ->
                Icon(
                    imageVector        = if (running) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = if (running) "Pause" else "Play",
                    modifier           = Modifier.size(36.dp),
                )
            }
        }

        IconButton(onClick = onSkip) {
            Icon(
                imageVector        = Icons.Rounded.SkipNext,
                contentDescription = "Skip phase",
                tint               = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier           = Modifier.size(28.dp),
            )
        }
    }
}

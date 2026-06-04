package com.twinklingtreasure.timer.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PictureInPicture
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.twinklingtreasure.timer.data.AppSettings
import com.twinklingtreasure.timer.data.TimerCycle
import com.twinklingtreasure.timer.data.TimerState
import com.twinklingtreasure.timer.ui.theme.DeepNavy
import com.twinklingtreasure.timer.ui.theme.NavyBlue
import com.twinklingtreasure.timer.ui.theme.OffWhite
import com.twinklingtreasure.timer.ui.theme.SurfaceLight
import com.twinklingtreasure.timer.util.formatTime

@Composable
fun MainScreen(
    uiState: TimerState,
    isInPipMode: Boolean,
    progressFraction: Float,
    settings: AppSettings,
    isDark: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onSkip: () -> Unit,
    onReset: () -> Unit,
    onEnterPip: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    if (isInPipMode) {
        PipContent(
            currentPhaseIndex = uiState.currentPhaseIndex,
            secondsRemaining  = uiState.secondsRemaining,
        )
        return
    }

    val phase  = TimerCycle.phases[uiState.currentPhaseIndex]
    val accent = if (isDark) phase.accentDark else phase.accentLight

    val gradient = if (isDark) {
        Brush.radialGradient(colors = listOf(NavyBlue, DeepNavy), radius = 1200f)
    } else {
        Brush.radialGradient(colors = listOf(SurfaceLight, OffWhite), radius = 1200f)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Background layer ─────────────────────────────────────
        if (settings.wallpaperUri.isNotEmpty()) {
            AsyncImage(
                model              = settings.wallpaperUri,
                contentDescription = null,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize(),
            )
            // Dark scrim: opacity = 1 - wallpaperOpacity, so slider "Bright" = less scrim
            Box(
                Modifier.fillMaxSize()
                    .background(DeepNavy.copy(alpha = 1f - settings.wallpaperOpacity))
            )
        } else {
            Box(Modifier.fillMaxSize().background(brush = gradient))
        }

        // ── UI content ───────────────────────────────────────────
        Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {

            IconButton(
                onClick  = onOpenSettings,
                modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
            ) {
                Icon(
                    imageVector        = Icons.Rounded.Settings,
                    contentDescription = "Settings",
                    tint               = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                )
            }

            IconButton(
                onClick  = onEnterPip,
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
            ) {
                Icon(
                    imageVector        = Icons.Rounded.PictureInPicture,
                    contentDescription = "Float",
                    tint               = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier            = Modifier.fillMaxSize(),
            ) {
                Spacer(Modifier.weight(0.6f))

                CircularProgressRing(
                    progress    = progressFraction,
                    accentColor = accent,
                    size        = 280.dp,
                    strokeWidth = 14.dp,
                ) {
                    AnimatedContent(
                        targetState    = uiState.currentPhaseIndex,
                        transitionSpec = {
                            (fadeIn(tween(400)) + scaleIn(tween(400), initialScale = 0.85f))
                                .togetherWith(fadeOut(tween(250)) + scaleOut(tween(250), targetScale = 1.1f))
                        },
                        label          = "phaseContent",
                    ) { idx ->
                        val p = TimerCycle.phases[idx]
                        val a = if (isDark) p.accentDark else p.accentLight
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = p.emoji, fontSize = 40.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text  = formatTime(uiState.secondsRemaining),
                                style = MaterialTheme.typography.displayMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text  = p.name.uppercase(),
                                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 3.sp),
                                color = a,
                            )
                        }
                    }
                }

                Spacer(Modifier.weight(0.3f))

                Text(
                    text      = phase.description,
                    style     = MaterialTheme.typography.bodyLarge,
                    color     = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.padding(horizontal = 32.dp),
                )

                Spacer(Modifier.weight(0.4f))

                TimerControls(
                    isRunning = uiState.isRunning,
                    onStart   = onStart,
                    onPause   = onPause,
                    onSkip    = onSkip,
                    onReset   = onReset,
                )

                Spacer(Modifier.weight(0.4f))

                PhasesStrip(
                    currentPhaseIndex = uiState.currentPhaseIndex,
                    secondsRemaining  = uiState.secondsRemaining,
                    modifier          = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

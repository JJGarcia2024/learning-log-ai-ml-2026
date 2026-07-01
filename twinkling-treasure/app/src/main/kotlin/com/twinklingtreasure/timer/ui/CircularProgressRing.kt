package com.twinklingtreasure.timer.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CircularProgressRing(
    progress: Float,
    accentColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 280.dp,
    strokeWidth: Dp = 14.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    val animatedProgress by animateFloatAsState(
        targetValue   = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label         = "ringProgress",
    )
    val animatedColor by animateColorAsState(
        targetValue   = accentColor,
        animationSpec = tween(durationMillis = 900),
        label         = "ringColor",
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size),
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val inset    = strokePx / 2f
            val arcSize  = Size(this.size.width - strokePx, this.size.height - strokePx)
            val topLeft  = Offset(inset, inset)

            // Track ring
            drawArc(
                color      = animatedColor.copy(alpha = 0.15f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter  = false,
                topLeft    = topLeft,
                size       = arcSize,
                style      = Stroke(width = strokePx, cap = StrokeCap.Round),
            )

            // Progress arc
            if (animatedProgress > 0f) {
                drawArc(
                    color      = animatedColor,
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter  = false,
                    topLeft    = topLeft,
                    size       = arcSize,
                    style      = Stroke(width = strokePx, cap = StrokeCap.Round),
                )

                // Glowing dot at arc tip
                val angleRad = Math.toRadians((-90.0 + animatedProgress * 360.0))
                val radius   = arcSize.width / 2f
                val cx = this.size.width / 2f + radius * cos(angleRad).toFloat()
                val cy = this.size.height / 2f + radius * sin(angleRad).toFloat()
                drawCircle(
                    color  = animatedColor,
                    radius = strokePx / 2f + 3f,
                    center = Offset(cx, cy),
                )
                drawCircle(
                    color  = Color.White.copy(alpha = 0.7f),
                    radius = strokePx / 2f - 2f,
                    center = Offset(cx, cy),
                )
            }
        }

        content()
    }
}

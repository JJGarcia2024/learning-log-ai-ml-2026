package com.twinklingtreasure.timer.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary          = DeepBlue,
    onPrimary        = Color.White,
    primaryContainer = PaleBlue,
    onPrimaryContainer = DeepNavy,
    secondary        = RichGold,
    onSecondary      = Color(0xFF1A1500),
    secondaryContainer = PaleGold,
    onSecondaryContainer = Color(0xFF1A1500),
    background       = OffWhite,
    onBackground     = Color(0xFF0D1B3E),
    surface          = Color.White,
    onSurface        = Color(0xFF0D1B3E),
    surfaceVariant   = SurfaceLight,
    onSurfaceVariant = Color(0xFF3A4A6A),
    outline          = Color(0xFFBBC5DC),
)

private val DarkColors = darkColorScheme(
    primary          = SkyBlue,
    onPrimary        = DeepNavy,
    primaryContainer = MediumBlue,
    onPrimaryContainer = PaleBlue,
    secondary        = Gold,
    onSecondary      = Color(0xFF1A1500),
    secondaryContainer = Color(0xFF3D2E00),
    onSecondaryContainer = PaleGold,
    background       = DeepNavy,
    onBackground     = Color(0xFFE0E8F8),
    surface          = SurfaceDark,
    onSurface        = Color(0xFFE0E8F8),
    surfaceVariant   = SurfaceDark2,
    onSurfaceVariant = Color(0xFFADB8D0),
    outline          = Color(0xFF3A4A6A),
)

@Composable
fun TwinklingTreasureTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography  = AppTypography,
        content     = content,
    )
}

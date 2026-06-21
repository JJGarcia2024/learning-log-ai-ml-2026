package com.jireh.sleeptimer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = BlueVivid,
    secondary = Gold,
    background = DarkBg,
    surface = DarkSurface,
    onPrimary = DarkText,
    onBackground = DarkText,
    onSurface = DarkText,
)

private val LightColors = lightColorScheme(
    primary = BlueVivid,
    secondary = GoldSoft,
    background = LightBg,
    surface = LightSurface,
    onPrimary = LightText,
    onBackground = LightText,
    onSurface = LightText,
)

@Composable
fun SleepTimerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}

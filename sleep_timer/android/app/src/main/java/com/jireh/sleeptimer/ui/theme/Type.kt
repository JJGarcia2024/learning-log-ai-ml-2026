package com.jireh.sleeptimer.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Google Sans is proprietary and not shipped here. Drop the .ttf files into
 * res/font/ (see res/font/README.md) and swap the line below to:
 *
 *   val GoogleSans = FontFamily(
 *       Font(R.font.google_sans_regular, FontWeight.Normal),
 *       Font(R.font.google_sans_medium,  FontWeight.Medium),
 *       Font(R.font.google_sans_bold,    FontWeight.Bold),
 *   )
 *
 * Until then we fall back to the system sans-serif so the project still builds.
 */
val GoogleSans: FontFamily = FontFamily.SansSerif

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = GoogleSans, fontWeight = FontWeight.Bold, fontSize = 64.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = GoogleSans, fontWeight = FontWeight.Bold, fontSize = 28.sp
    ),
    titleLarge = TextStyle(
        fontFamily = GoogleSans, fontWeight = FontWeight.Bold, fontSize = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = GoogleSans, fontWeight = FontWeight.Normal, fontSize = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = GoogleSans, fontWeight = FontWeight.Medium, fontSize = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = GoogleSans, fontWeight = FontWeight.Medium, fontSize = 11.sp,
        letterSpacing = 1.5.sp
    ),
)

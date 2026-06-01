package com.twinklingtreasure.timer.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.twinklingtreasure.timer.R

val GoogleSans = FontFamily(
    Font(R.font.google_sans_regular, FontWeight.Normal),
    Font(R.font.google_sans_medium,  FontWeight.Medium),
    Font(R.font.google_sans_bold,    FontWeight.Bold),
)

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = GoogleSans,
        fontWeight = FontWeight.Bold,
        fontSize   = 72.sp,
        lineHeight = 80.sp,
    ),
    displayMedium = TextStyle(
        fontFamily = GoogleSans,
        fontWeight = FontWeight.Bold,
        fontSize   = 56.sp,
        lineHeight = 64.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = GoogleSans,
        fontWeight = FontWeight.Medium,
        fontSize   = 32.sp,
        lineHeight = 40.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = GoogleSans,
        fontWeight = FontWeight.Medium,
        fontSize   = 24.sp,
        lineHeight = 32.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = GoogleSans,
        fontWeight = FontWeight.Medium,
        fontSize   = 20.sp,
        lineHeight = 28.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = GoogleSans,
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 24.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = GoogleSans,
        fontWeight = FontWeight.Medium,
        fontSize   = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = GoogleSans,
        fontWeight = FontWeight.Normal,
        fontSize   = 10.sp,
        lineHeight = 14.sp,
    ),
)

package com.aleon.proyectocellcli.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.aleon.proyectocellcli.R

// 1. Define the FontFamily using the XML resource
val AdventPro = FontFamily(
    Font(R.font.advent_pro_regular, FontWeight.Normal),
    Font(R.font.advent_pro_medium, FontWeight.Medium),
    Font(R.font.advent_pro_bold, FontWeight.Bold)
)

// 2. Update the Typography object to use the new FontFamily
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = AdventPro,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = AdventPro,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    // For Buttons
    labelLarge = TextStyle(
        fontFamily = AdventPro,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelSmall = TextStyle(
        fontFamily = AdventPro,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    // For Dashboard Date Headers
    titleSmall = TextStyle(
        fontFamily = AdventPro,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = AdventPro,
        fontWeight = FontWeight.Bold
    ),
    titleMedium = TextStyle(
        fontFamily = AdventPro,
        fontWeight = FontWeight.Bold
    )
)
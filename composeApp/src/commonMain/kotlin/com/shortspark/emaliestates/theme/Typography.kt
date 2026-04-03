package com.shortspark.emaliestates.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import emaliestates.composeapp.generated.resources.Poppins_Black
import emaliestates.composeapp.generated.resources.Poppins_Bold
import emaliestates.composeapp.generated.resources.Poppins_ExtraBold
import emaliestates.composeapp.generated.resources.Poppins_ExtraLight
import emaliestates.composeapp.generated.resources.Poppins_Light
import emaliestates.composeapp.generated.resources.Poppins_Medium
import emaliestates.composeapp.generated.resources.Poppins_Regular
import emaliestates.composeapp.generated.resources.Poppins_SemiBold
import emaliestates.composeapp.generated.resources.Poppins_Thin
import emaliestates.composeapp.generated.resources.Res
import emaliestates.composeapp.generated.resources.Roboto_Bold
import org.jetbrains.compose.resources.Font

@Composable
fun poppinsFontFamily() = FontFamily(
    Font(Res.font.Poppins_Thin, FontWeight.Thin),
    Font(Res.font.Poppins_ExtraLight, FontWeight.ExtraLight),
    Font(Res.font.Poppins_Light, FontWeight.Light),
    Font(Res.font.Poppins_Regular, FontWeight.Normal),
    Font(Res.font.Poppins_Medium, FontWeight.Medium),
    Font(Res.font.Poppins_SemiBold, FontWeight.SemiBold),
    Font(Res.font.Poppins_Bold, FontWeight.Bold),
    Font(Res.font.Poppins_ExtraBold, FontWeight.ExtraBold),
    Font(Res.font.Poppins_Black, FontWeight.Black),
)

@Composable
fun robotoFontFamily() = FontFamily(
    Font(Res.font.Roboto_Bold, FontWeight.W900),
)

val typography @Composable get() = Typography(
    bodyLarge = TextStyle(
        fontFamily = poppinsFontFamily(),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    displayLarge = TextStyle(
        fontFamily = robotoFontFamily(),
        fontWeight = FontWeight.W900,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp
    ),
    displayMedium = TextStyle(
        fontFamily = poppinsFontFamily(),
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = poppinsFontFamily(),
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = poppinsFontFamily(),
        fontWeight = FontWeight.Light,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = poppinsFontFamily(),
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily = poppinsFontFamily(),
        fontWeight = FontWeight.Thin,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
)
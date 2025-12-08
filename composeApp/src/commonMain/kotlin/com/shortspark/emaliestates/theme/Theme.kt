package com.shortspark.emaliestates.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DarkAppColors.Primary,
    primaryContainer = DarkAppColors.PrimaryVariant,
    secondary = DarkAppColors.Secondary,
    background = DarkAppColors.Background,
    surface = DarkAppColors.Surface,
    error = DarkAppColors.Error,
    onPrimary = DarkAppColors.OnPrimary,
    onSecondary = DarkAppColors.OnSecondary,
    onBackground = DarkAppColors.OnBackground,
    onSurface = DarkAppColors.OnSurface,
    onError = DarkAppColors.OnError
)

private val LightColorScheme = lightColorScheme(
    primary = LightAppColors.Primary,
    primaryContainer = LightAppColors.PrimaryVariant,
    secondary = LightAppColors.Secondary,
    background = LightAppColors.Background,
    surface = LightAppColors.Surface,
    error = LightAppColors.Error,
    onPrimary = LightAppColors.OnPrimary,
    onSecondary = LightAppColors.OnSecondary,
    onBackground = LightAppColors.OnBackground,
    onSurface = LightAppColors.OnSurface,
    onError = LightAppColors.OnError
)

@Composable
fun EmaliEstatesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}

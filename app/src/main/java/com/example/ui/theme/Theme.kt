package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val NoirDarkColorScheme = darkColorScheme(
    primary = AccentAmber,
    onPrimary = NoirBackground,
    primaryContainer = NoirSurfaceInteractive,
    onPrimaryContainer = AccentAmberLight,
    secondary = AccentBlue,
    onSecondary = NoirBackground,
    secondaryContainer = NoirSurfaceElevated,
    onSecondaryContainer = AccentBlueGlow,
    tertiary = AccentAmberMuted,
    onTertiary = TextPrimary,
    background = NoirBackground,
    onBackground = TextPrimary,
    surface = NoirSurface,
    onSurface = TextPrimary,
    surfaceVariant = NoirSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    surfaceContainer = NoirSurfaceElevated,
    surfaceContainerHigh = NoirSurfaceInteractive,
    outline = DividerColor,
    outlineVariant = NoirSurfaceElevated,
    error = CrimeSceneRed,
    onError = TextPrimary
)

private val NoirLightColorScheme = lightColorScheme(
    primary = AccentAmberMuted,
    onPrimary = LightSurface,
    primaryContainer = LightSurfaceInteractive,
    onPrimaryContainer = LightTextPrimary,
    secondary = AccentBlueMuted,
    onSecondary = LightSurface,
    secondaryContainer = LightSurfaceElevated,
    onSecondaryContainer = LightTextPrimary,
    tertiary = AccentAmber,
    onTertiary = LightSurface,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = LightTextSecondary,
    surfaceContainer = LightSurfaceElevated,
    surfaceContainerHigh = LightSurfaceInteractive,
    outline = LightDivider,
    outlineVariant = LightSurfaceElevated,
    error = CrimeSceneRed,
    onError = LightSurface
)

@Composable
fun CaseZeroTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) NoirDarkColorScheme else NoirLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = CaseZeroTypography,
        content = content
    )
}

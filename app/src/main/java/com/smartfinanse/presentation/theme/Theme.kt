package com.smartfinanse.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = GreenContainer,
    onPrimaryContainer = GreenPrimary,
    secondary = GreenLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8F5E9),
    onSecondaryContainer = GreenDark,
    tertiary = FabAccent,
    onTertiary = FabOnAccentLight,
    tertiaryContainer = Color(0xFFFFE0B2),
    onTertiaryContainer = Color(0xFFE65100),
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    background = DashboardBackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    outlineVariant = Color(0xFFCAC4D0),
    scrim = Color(0xFF000000),
    inverseSurface = OnSurfaceLight,
    inverseOnSurface = SurfaceLight,
    inversePrimary = GreenLight,
    surfaceTint = GreenPrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = GreenLight,
    onPrimary = GreenDark,
    primaryContainer = GreenDarkContainer,
    onPrimaryContainer = GreenContainer,
    secondary = Color(0xFF81C784),
    onSecondary = GreenDark,
    secondaryContainer = GreenDarkContainer,
    onSecondaryContainer = GreenContainer,
    tertiary = FabAccent,
    onTertiary = FabOnAccentDark,
    tertiaryContainer = Color(0xFF5D4037),
    onTertiaryContainer = Color(0xFFFFE0B2),
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    background = DashboardBackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = Color(0xFF49454F),
    scrim = Color(0xFF000000),
    inverseSurface = OnSurfaceDark,
    inverseOnSurface = SurfaceDark,
    inversePrimary = GreenPrimary,
    surfaceTint = GreenLight
)

object SmartFinanseTheme {
    val extraColors: SmartFinanseExtraColors
        @Composable
        get() = LocalSmartFinanseExtraColors.current
}

@Composable
fun SmartFinanseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extraColors = if (darkTheme) DarkExtraColors else LightExtraColors

    CompositionLocalProvider(LocalSmartFinanseExtraColors provides extraColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = SmartFinanseTypography,
            content = content
        )
    }
}

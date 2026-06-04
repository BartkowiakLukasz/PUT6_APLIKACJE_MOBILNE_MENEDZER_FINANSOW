package com.smartfinanse.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Dashboard-only accents. Global chrome uses [androidx.compose.material3.MaterialTheme.colorScheme].
 */
@Immutable
data class SmartFinanseExtraColors(
    /** Tinted surface for the chart card only */
    val chartCardBackground: Color
)

val LightExtraColors = SmartFinanseExtraColors(
    chartCardBackground = ChartCardLight
)

val DarkExtraColors = SmartFinanseExtraColors(
    chartCardBackground = ChartCardDark
)

val LocalSmartFinanseExtraColors = staticCompositionLocalOf { LightExtraColors }

package com.smartfinanse.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Brand-specific colors not covered by Material [androidx.compose.material3.ColorScheme].
 * Use via [SmartFinanseTheme.extraColors] in Composables.
 */
@Immutable
data class SmartFinanseExtraColors(
    val mintHeader: Color,
    val onMintHeader: Color,
    val dashboardBackground: Color,
    val fabContainer: Color,
    val fabContent: Color,
    val filterChipSelected: Color
)

val LightExtraColors = SmartFinanseExtraColors(
    mintHeader = MintPrimary,
    onMintHeader = MintOnPrimary,
    dashboardBackground = DashboardBackgroundLight,
    fabContainer = FabAccent,
    fabContent = FabOnAccentLight,
    filterChipSelected = MintPrimary
)

val DarkExtraColors = SmartFinanseExtraColors(
    mintHeader = MintPrimaryDark,
    onMintHeader = MintOnPrimaryDark,
    dashboardBackground = DashboardBackgroundDark,
    fabContainer = FabAccent,
    fabContent = FabOnAccentDark,
    filterChipSelected = MintPrimaryDark
)

val LocalSmartFinanseExtraColors = staticCompositionLocalOf { LightExtraColors }

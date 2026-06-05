package com.smartfinanse.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class SmartFinanseExtraColors(
    val chartCardBackground: Color,
    val dashboardHeroGradientStart: Color,
    val dashboardHeroGradientEnd: Color,
    val dashboardHeroOnGradient: Color,
    val segmentTrack: Color,
    val segmentThumb: Color,
    val segmentTextSelected: Color,
    val segmentTextUnselected: Color
)

val LightExtraColors = SmartFinanseExtraColors(
    chartCardBackground = ChartCardLight,
    dashboardHeroGradientStart = HeroGradientStartLight,
    dashboardHeroGradientEnd = HeroGradientEndLight,
    dashboardHeroOnGradient = Color.White,
    segmentTrack = SegmentTrackLight,
    segmentThumb = SegmentThumbLight,
    segmentTextSelected = OnSurfaceLight,
    segmentTextUnselected = SegmentTextInactiveLight
)

val DarkExtraColors = SmartFinanseExtraColors(
    chartCardBackground = ChartCardDark,
    dashboardHeroGradientStart = HeroGradientStartDark,
    dashboardHeroGradientEnd = HeroGradientEndDark,
    dashboardHeroOnGradient = Color.White,
    segmentTrack = SegmentTrackDark,
    segmentThumb = SegmentThumbDark,
    segmentTextSelected = Color.White,
    segmentTextUnselected = SegmentTextInactiveDark
)

val LocalSmartFinanseExtraColors = staticCompositionLocalOf { LightExtraColors }

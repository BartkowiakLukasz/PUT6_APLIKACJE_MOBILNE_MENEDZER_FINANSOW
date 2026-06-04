package com.smartfinanse.presentation.theme

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Syncs status/navigation bar icon appearance with the app theme
 * (including user override: Jasny / Ciemny in Settings, not only system).
 * Call inside [SmartFinanseTheme] after [enableEdgeToEdge] on the Activity.
 */
@Composable
fun ConfigureSystemBars(darkTheme: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return

    SideEffect {
        val window = (view.context as? Activity)?.window ?: return@SideEffect
        val insetsController = WindowCompat.getInsetsController(window, view)
        val useLightIcons = darkTheme
        insetsController.isAppearanceLightStatusBars = !useLightIcons
        insetsController.isAppearanceLightNavigationBars = !useLightIcons
    }
}

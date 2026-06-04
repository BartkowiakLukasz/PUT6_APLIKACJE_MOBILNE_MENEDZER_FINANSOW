package com.smartfinanse.presentation.theme

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import com.smartfinanse.R
import com.smartfinanse.domain.repository.AppTheme

/**
 * Optional extra [Activity.setTheme] for splash (main fix is [ThemePreferenceApplier] in Application).
 */
fun Activity.applyLaunchSplashTheme() {
    val useDarkSplash = ThemePreferenceApplier.useDarkSplash(
        appTheme = readSavedAppTheme(this),
        systemInDarkMode = resources.configuration.isNightModeActive
    )

    setTheme(
        if (useDarkSplash) R.style.Theme_SmartFinanse_Splash_Dark
        else R.style.Theme_SmartFinanse_Splash_Light
    )
}

private fun readSavedAppTheme(context: Context): AppTheme {
    val themeName = context.getSharedPreferences("smart_finanse_prefs", Context.MODE_PRIVATE)
        .getString("app_theme", AppTheme.SYSTEM.name)
        ?: AppTheme.SYSTEM.name
    return try {
        AppTheme.valueOf(themeName)
    } catch (_: IllegalArgumentException) {
        AppTheme.SYSTEM
    }
}

private val Configuration.isNightModeActive: Boolean
    get() = (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

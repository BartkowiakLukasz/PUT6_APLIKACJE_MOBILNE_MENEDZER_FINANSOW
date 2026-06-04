package com.smartfinanse.presentation.theme

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.smartfinanse.domain.repository.AppTheme

private const val PREFS_NAME = "smart_finanse_prefs"
private const val KEY_APP_THEME = "app_theme"

object ThemePreferenceApplier {

    fun apply(context: Context) {
        AppCompatDelegate.setDefaultNightMode(resolveNightMode(context))
    }

    fun nightModeFor(appTheme: AppTheme, systemInDarkMode: Boolean): Int = when (appTheme) {
        AppTheme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
        AppTheme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
        AppTheme.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    fun useDarkSplash(appTheme: AppTheme, systemInDarkMode: Boolean): Boolean = when (appTheme) {
        AppTheme.DARK -> true
        AppTheme.LIGHT -> false
        AppTheme.SYSTEM -> systemInDarkMode
    }

    private fun resolveNightMode(context: Context): Int {
        val themeName = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_APP_THEME, AppTheme.SYSTEM.name)
            ?: AppTheme.SYSTEM.name

        val appTheme = try {
            AppTheme.valueOf(themeName)
        } catch (_: IllegalArgumentException) {
            AppTheme.SYSTEM
        }

        val systemDark = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES

        return nightModeFor(appTheme, systemDark)
    }
}

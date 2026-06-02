package com.smartfinanse.domain.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class AppTheme {
    SYSTEM, LIGHT, DARK
}

enum class Currency(val symbol: String) {
    PLN("zł"), USD("$"), EUR("€")
}

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("smart_finanse_prefs", Context.MODE_PRIVATE)

    private val _theme = MutableStateFlow(getSavedTheme())
    val theme: StateFlow<AppTheme> = _theme.asStateFlow()

    private val _currency = MutableStateFlow(getSavedCurrency())
    val currency: StateFlow<Currency> = _currency.asStateFlow()

    fun setTheme(theme: AppTheme) {
        prefs.edit().putString("app_theme", theme.name).apply()
        _theme.value = theme
    }

    fun setCurrency(currency: Currency) {
        prefs.edit().putString("app_currency", currency.name).apply()
        _currency.value = currency
    }

    private fun getSavedTheme(): AppTheme {
        val themeString = prefs.getString("app_theme", AppTheme.SYSTEM.name) ?: AppTheme.SYSTEM.name
        return try {
            AppTheme.valueOf(themeString)
        } catch (e: Exception) {
            AppTheme.SYSTEM
        }
    }

    private fun getSavedCurrency(): Currency {
        val currencyString = prefs.getString("app_currency", Currency.PLN.name) ?: Currency.PLN.name
        return try {
            Currency.valueOf(currencyString)
        } catch (e: Exception) {
            Currency.PLN
        }
    }
}

package com.smartfinanse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartfinanse.domain.repository.AppTheme
import com.smartfinanse.domain.repository.UserPreferencesRepository
import com.smartfinanse.presentation.common.MoneyFormatter
import com.smartfinanse.presentation.navigation.SmartFinanseNavHost
import com.smartfinanse.presentation.theme.SmartFinanseTheme
import com.smartfinanse.presentation.theme.applyLaunchSplashTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        applyLaunchSplashTheme()
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        setContent {
            val theme by preferencesRepository.theme.collectAsStateWithLifecycle()
            val currency by preferencesRepository.currency.collectAsStateWithLifecycle()

            MoneyFormatter.currentCurrencySymbol = currency.symbol

            val isDarkTheme = when (theme) {
                AppTheme.SYSTEM -> isSystemInDarkTheme()
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
            }

            SmartFinanseTheme(darkTheme = isDarkTheme) {
                com.smartfinanse.presentation.navigation.SmartFinanseRoot()
            }
        }
    }
}

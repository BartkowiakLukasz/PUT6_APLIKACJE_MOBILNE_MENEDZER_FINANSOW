package com.smartfinanse

import android.app.Application
import android.content.Context
import com.smartfinanse.domain.usecase.SeedCategoriesUseCase
import com.smartfinanse.presentation.theme.ThemePreferenceApplier
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@HiltAndroidApp
class SmartFinanseApplication : Application() {

    @Inject
    lateinit var seedCategoriesUseCase: SeedCategoriesUseCase

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun attachBaseContext(base: Context) {
        ThemePreferenceApplier.apply(base)
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        com.smartfinanse.utils.FileLogger.init(this)
        applicationScope.launch {
            seedCategoriesUseCase()
        }
    }
}

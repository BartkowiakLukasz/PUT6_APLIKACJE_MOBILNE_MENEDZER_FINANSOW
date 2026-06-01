package com.smartfinanse

import android.app.Application
import com.smartfinanse.domain.usecase.SeedCategoriesUseCase
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

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            seedCategoriesUseCase()
        }
    }
}

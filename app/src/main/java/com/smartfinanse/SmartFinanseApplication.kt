package com.smartfinanse

import android.app.Application
import android.content.Context
import com.smartfinanse.domain.usecase.SeedCategoriesUseCase
import com.smartfinanse.domain.usecase.SeedStoresUseCase
import com.smartfinanse.presentation.theme.ThemePreferenceApplier
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import androidx.lifecycle.ProcessLifecycleOwner
import com.smartfinanse.util.SessionTimeoutManager

@HiltAndroidApp
class SmartFinanseApplication : Application() {

    @Inject
    lateinit var sessionTimeoutManager: SessionTimeoutManager

    @Inject
    lateinit var seedCategoriesUseCase: SeedCategoriesUseCase

    @Inject
    lateinit var seedStoresUseCase: SeedStoresUseCase

    @Inject
    lateinit var seedSubscriptionCategoriesUseCase: com.smartfinanse.domain.usecase.SeedSubscriptionCategoriesUseCase

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun attachBaseContext(base: Context) {
        ThemePreferenceApplier.apply(base)
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        
        ProcessLifecycleOwner.get().lifecycle.addObserver(sessionTimeoutManager)
        com.smartfinanse.utils.FileLogger.init(this)
        
        com.smartfinanse.domain.manager.SubscriptionNotificationManager.createNotificationChannel(this)
        com.smartfinanse.domain.worker.WorkerScheduler.scheduleSubscriptionReminders(this)

        applicationScope.launch {
            seedCategoriesUseCase()
            seedStoresUseCase()
            seedSubscriptionCategoriesUseCase()
        }
    }
}

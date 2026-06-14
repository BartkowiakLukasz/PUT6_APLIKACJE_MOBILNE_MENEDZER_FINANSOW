package com.smartfinanse.domain.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object WorkerScheduler {
    private const val SUBSCRIPTION_REMINDER_WORK = "subscription_reminder_work"

    fun scheduleSubscriptionReminders(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<SubscriptionReminderWorker>(
            24, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SUBSCRIPTION_REMINDER_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}

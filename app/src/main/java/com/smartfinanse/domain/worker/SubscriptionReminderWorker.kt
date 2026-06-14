package com.smartfinanse.domain.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.smartfinanse.domain.manager.SubscriptionNotificationManager
import com.smartfinanse.domain.model.BillingCycle
import com.smartfinanse.domain.repository.SubscriptionRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar
import java.util.concurrent.TimeUnit

class SubscriptionReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface SubscriptionWorkerEntryPoint {
        fun subscriptionRepository(): SubscriptionRepository
    }

    override suspend fun doWork(): Result {
        try {
            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                SubscriptionWorkerEntryPoint::class.java
            )
            val repository = entryPoint.subscriptionRepository()
            val subscriptions = repository.getAllSubscriptions().firstOrNull() ?: emptyList()

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val todayStart = calendar.timeInMillis

            subscriptions.forEach { subscription ->
                val reminderDays = SubscriptionNotificationLogic.shouldSendReminder(
                    startDate = subscription.startDate,
                    billingCycle = subscription.billingCycle,
                    todayStartMillis = todayStart
                )

                if (reminderDays == 7) {
                    SubscriptionNotificationManager.sendNotification(
                        context = applicationContext,
                        notificationId = subscription.id.toInt() * 10 + 7,
                        title = "Zbliża się płatność: ${subscription.serviceName}",
                        content = "Za 7 dni zostanie pobrana opłata w wysokości ${formatAmount(subscription.amount)}"
                    )
                } else if (reminderDays == 1) {
                    SubscriptionNotificationManager.sendNotification(
                        context = applicationContext,
                        notificationId = subscription.id.toInt() * 10 + 1,
                        title = "Jutro płatność: ${subscription.serviceName}",
                        content = "Przygotuj środki na kwotę ${formatAmount(subscription.amount)}"
                    )
                }
            }
            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }
    }

    private fun formatAmount(amountInGrosze: Long): String {
        val pln = amountInGrosze / 100
        val grosze = amountInGrosze % 100
        return String.format("%d,%02d PLN", pln, grosze)
    }
}

package com.smartfinanse.domain.usecase

import com.smartfinanse.domain.model.BillingCycle
import com.smartfinanse.domain.model.Transaction
import com.smartfinanse.domain.repository.SubscriptionRepository
import com.smartfinanse.domain.repository.TransactionRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class ProcessSubscriptionsUseCase @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke() {
        val subscriptions = subscriptionRepository.getAllSubscriptionsSync()
        val today = LocalDate.now()

        for (sub in subscriptions) {
            val startLocalDate = Instant.ofEpochMilli(sub.startDate).atZone(ZoneId.systemDefault()).toLocalDate()
            val maxDateMillis = transactionRepository.getMaxDateForSubscription(sub.id)
            
            var nextPaymentDate = if (maxDateMillis != null) {
                val lastPaymentDate = Instant.ofEpochMilli(maxDateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
                when (sub.billingCycle) {
                    BillingCycle.MONTHLY -> lastPaymentDate.plusMonths(1)
                    BillingCycle.YEARLY -> lastPaymentDate.plusYears(1)
                }
            } else {
                startLocalDate
            }

            while (!nextPaymentDate.isAfter(today)) {
                val transactionDateMillis = nextPaymentDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                
                val transaction = Transaction(
                    id = 0,
                    categoryId = null, // Or try to map from subscription category
                    amount = sub.amount,
                    description = sub.serviceName,
                    date = transactionDateMillis,
                    isCash = false,
                    isRecurring = true,
                    location = null,
                    receiptImageUri = null,
                    storeId = null,
                    subscriptionId = sub.id
                )
                
                transactionRepository.addTransaction(transaction)

                nextPaymentDate = when (sub.billingCycle) {
                    BillingCycle.MONTHLY -> nextPaymentDate.plusMonths(1)
                    BillingCycle.YEARLY -> nextPaymentDate.plusYears(1)
                }
            }
        }
    }
}

package com.smartfinanse.domain.usecase

import com.smartfinanse.domain.model.BillingCycle
import com.smartfinanse.domain.model.Subscription
import com.smartfinanse.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class SubscriptionItem(
    val subscription: Subscription,
    val nextRenewalDateMillis: Long
)

class GetSubscriptionsUseCase @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
) {
    operator fun invoke(): Flow<List<SubscriptionItem>> {
        return subscriptionRepository.getAllSubscriptions()
            .map { list ->
                list.map { sub ->
                    val nextRenewal = calculateNextRenewal(sub.startDate, sub.billingCycle)
                    SubscriptionItem(sub, nextRenewal)
                }.sortedBy { it.nextRenewalDateMillis }
            }
    }

    private fun calculateNextRenewal(startDateMillis: Long, cycle: BillingCycle): Long {
        val today = LocalDate.now()
        val startLocalDate = Instant.ofEpochMilli(startDateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
        
        var nextDate = startLocalDate
        while (!nextDate.isAfter(today)) {
            nextDate = when (cycle) {
                BillingCycle.MONTHLY -> nextDate.plusMonths(1)
                BillingCycle.YEARLY -> nextDate.plusYears(1)
            }
        }
        return nextDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}

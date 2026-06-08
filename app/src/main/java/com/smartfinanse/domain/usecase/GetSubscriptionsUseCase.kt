package com.smartfinanse.domain.usecase

import com.smartfinanse.domain.model.TransactionWithCategory
import com.smartfinanse.domain.repository.TransactionRepository
import com.smartfinanse.domain.util.SubscriptionCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class SubscriptionItem(
    val transactionWithCategory: TransactionWithCategory,
    val nextRenewalDateMillis: Long
)

class GetSubscriptionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(): Flow<List<SubscriptionItem>> {
        return transactionRepository.getAllTransactions()
            .map { list ->
                list.filter { it.transaction.isRecurring }
                    .map { item ->
                        val nextRenewal = SubscriptionCalculator.calculateNextRenewal(
                            startDateMillis = item.transaction.date,
                            interval = item.transaction.recurringInterval
                        )
                        SubscriptionItem(item, nextRenewal)
                    }
                    .sortedBy { it.nextRenewalDateMillis }
            }
    }
}

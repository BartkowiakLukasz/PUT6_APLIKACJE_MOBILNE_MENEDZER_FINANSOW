package com.smartfinanse.domain.repository

import com.smartfinanse.domain.model.Transaction
import com.smartfinanse.domain.model.TransactionWithCategory
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<TransactionWithCategory>>
    fun getTransactionsBetweenDates(startDate: Long, endDate: Long): Flow<List<Transaction>>
    suspend fun getMaxDateForSubscription(subscriptionId: Long): Long?
    suspend fun addTransaction(transaction: Transaction)
}

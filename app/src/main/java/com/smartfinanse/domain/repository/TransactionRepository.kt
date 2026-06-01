package com.smartfinanse.domain.repository

import com.smartfinanse.domain.model.TransactionWithCategory
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<TransactionWithCategory>>
    suspend fun addTransaction(transaction: com.smartfinanse.domain.model.Transaction)
}

package com.smartfinanse.data.repository

import com.smartfinanse.data.local.dao.TransactionDao
import com.smartfinanse.data.local.mapper.toDomain
import com.smartfinanse.data.local.mapper.toEntity
import com.smartfinanse.domain.model.TransactionWithCategory
import com.smartfinanse.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<TransactionWithCategory>> =
        transactionDao.getAllWithDetails().map { items ->
            items.map { it.toDomain() }
        }

    override fun getTransactionsBetweenDates(startDate: Long, endDate: Long): Flow<List<com.smartfinanse.domain.model.Transaction>> =
        transactionDao.getTransactionsBetweenDates(startDate, endDate).map { items ->
            items.map { it.transaction.toDomain() }
        }

    override suspend fun getMaxDateForSubscription(subscriptionId: Long): Long? =
        transactionDao.getMaxDateForSubscription(subscriptionId)

    override suspend fun addTransaction(transaction: com.smartfinanse.domain.model.Transaction) {
        transactionDao.insertTransaction(transaction.toEntity())
    }
}

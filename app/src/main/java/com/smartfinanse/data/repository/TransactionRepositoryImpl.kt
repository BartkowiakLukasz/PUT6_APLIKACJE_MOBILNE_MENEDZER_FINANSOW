package com.smartfinanse.data.repository

import com.smartfinanse.data.local.dao.TransactionDao
import com.smartfinanse.data.local.mapper.toDomain
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
}

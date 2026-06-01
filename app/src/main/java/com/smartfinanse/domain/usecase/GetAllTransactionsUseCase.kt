package com.smartfinanse.domain.usecase

import com.smartfinanse.domain.model.TransactionWithCategory
import com.smartfinanse.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(): Flow<List<TransactionWithCategory>> =
        transactionRepository.getAllTransactions()
}

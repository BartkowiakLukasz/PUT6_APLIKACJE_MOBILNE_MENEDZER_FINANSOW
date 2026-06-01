package com.smartfinanse.domain.usecase

import com.smartfinanse.domain.model.Transaction
import com.smartfinanse.domain.repository.TransactionRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        transactionRepository.addTransaction(transaction)
    }
}

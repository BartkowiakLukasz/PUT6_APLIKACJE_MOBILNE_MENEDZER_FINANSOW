package com.smartfinanse.domain.model

data class TransactionWithCategory(
    val transaction: Transaction,
    val category: Category?,
    val store: Store? = null
)

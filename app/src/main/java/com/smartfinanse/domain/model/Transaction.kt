package com.smartfinanse.domain.model

data class Transaction(
    val id: Long,
    val categoryId: Long?,
    val amount: Long,
    val description: String,
    val date: Long,
    val isCash: Boolean,
    val isRecurring: Boolean,
    val location: String?,
    val receiptImageUri: String?
)

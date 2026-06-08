package com.smartfinanse.data.local.mapper

import com.smartfinanse.data.local.entity.TransactionEntity
import com.smartfinanse.data.local.entity.TransactionWithDetails
import com.smartfinanse.domain.model.Transaction
import com.smartfinanse.domain.model.TransactionWithCategory

fun TransactionEntity.toDomain(): Transaction = Transaction(
    id = id,
    categoryId = categoryId,
    amount = amount,
    description = description,
    date = date,
    isCash = isCash,
    isRecurring = isRecurring,
    location = location,
    receiptImageUri = receiptImageUri,
    recurringInterval = recurringInterval
)

fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    categoryId = categoryId,
    amount = amount,
    description = description,
    date = date,
    isCash = isCash,
    isRecurring = isRecurring,
    location = location,
    receiptImageUri = receiptImageUri
).apply { this.recurringInterval = this@toEntity.recurringInterval }

fun TransactionWithDetails.toDomain(): TransactionWithCategory = TransactionWithCategory(
    transaction = transaction.toDomain(),
    category = category?.toDomain()
)

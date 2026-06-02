package com.smartfinanse.data.export

import com.smartfinanse.data.local.entity.CategoryEntity
import com.smartfinanse.data.local.entity.TransactionEntity

data class BackupData(
    val categories: List<CategoryEntity>,
    val transactions: List<TransactionEntity>
)

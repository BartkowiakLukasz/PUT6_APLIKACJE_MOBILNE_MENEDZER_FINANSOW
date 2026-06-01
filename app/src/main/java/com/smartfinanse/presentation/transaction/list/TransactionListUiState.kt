package com.smartfinanse.presentation.transaction.list

data class TransactionListUiState(
    val transactions: List<TransactionItemUi> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class TransactionItemUi(
    val id: Long,
    val title: String,
    val categoryName: String?,
    val amountFormatted: String,
    val dateFormatted: String,
    val isCash: Boolean
)

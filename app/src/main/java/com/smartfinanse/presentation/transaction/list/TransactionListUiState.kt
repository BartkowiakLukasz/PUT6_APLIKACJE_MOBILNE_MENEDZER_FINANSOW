package com.smartfinanse.presentation.transaction.list

import com.smartfinanse.domain.model.Category
import com.smartfinanse.presentation.dashboard.TimeFilter

data class TransactionListUiState(
    val groupedTransactions: Map<String, List<TransactionItemUi>> = emptyMap(),
    val categories: List<Category> = emptyList(), // These will now be filtered by the search query
    val categorySearchQuery: String = "",
    val selectedCategoryId: Long? = null,
    val selectedTimeFilter: TimeFilter = TimeFilter.MONTH,
    val customStartDate: Long? = null,
    val customEndDate: Long? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class TransactionItemUi(
    val id: Long,
    val title: String,
    val categoryName: String?,
    val categoryId: Long?,
    val amountFormatted: String,
    val dateFormatted: String,
    val isCash: Boolean
)

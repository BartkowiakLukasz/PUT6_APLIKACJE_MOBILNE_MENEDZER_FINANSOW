package com.smartfinanse.presentation.transaction.add

import com.smartfinanse.domain.model.Category

data class AddTransactionUiState(
    val amount: String = "",
    val description: String = "",
    val date: Long = System.currentTimeMillis(),
    val isCash: Boolean = false,
    val isRecurring: Boolean = false,
    val isExpense: Boolean = true,
    val selectedCategoryId: Long? = null,
    val categorySearchQuery: String = "",
    val categories: List<Category> = emptyList(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val showAddCategoryDialog: Boolean = false,
    val amountError: String? = null,
    val categoryError: String? = null,
    val globalError: String? = null
)

package com.smartfinanse.presentation.transaction.add

import com.smartfinanse.domain.model.Category
import com.smartfinanse.domain.model.Store

data class AddTransactionUiState(
    val amount: String = "",
    val description: String = "",
    val date: Long = System.currentTimeMillis(),
    val isCash: Boolean = false,
    val isRecurring: Boolean = false,
    val isExpense: Boolean = true,
    val isSubscription: Boolean = false,
    val selectedCategoryId: Long? = null,
    val categorySearchQuery: String = "",
    val categories: List<Category> = emptyList(),
    val selectedStoreId: Long? = null,
    val storeSearchQuery: String = "",
    val recentStores: List<Store> = emptyList(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val showAddCategoryDialog: Boolean = false,
    val amountError: String? = null,
    val categoryError: String? = null,
    val globalError: String? = null
)

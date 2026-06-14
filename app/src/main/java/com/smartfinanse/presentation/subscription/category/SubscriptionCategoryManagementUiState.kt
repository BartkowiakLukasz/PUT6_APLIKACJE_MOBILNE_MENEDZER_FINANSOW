package com.smartfinanse.presentation.subscription.category

import com.smartfinanse.domain.model.SubscriptionCategory

data class SubscriptionCategoryManagementUiState(
    val isLoading: Boolean = true,
    val categories: List<SubscriptionCategory> = emptyList(),
    val filteredCategories: List<SubscriptionCategory> = emptyList(),
    val searchQuery: String = "",
    val isSheetOpen: Boolean = false,
    val isEditing: Boolean = false,
    val editingCategoryId: Long? = null,
    val nameInput: String = "",
    val selectedIconName: String = "ic_entertainment",
    val selectedColorHex: String = "#E50914",
    val showDeleteConfirmationFor: SubscriptionCategory? = null
)

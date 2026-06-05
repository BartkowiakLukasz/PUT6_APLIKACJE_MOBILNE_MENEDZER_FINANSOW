package com.smartfinanse.presentation.category

import com.smartfinanse.domain.model.Category

data class CategoryManagementUiState(
    val isLoading: Boolean = true,
    val categories: List<Category> = emptyList(),
    val managingExpenses: Boolean = true,

    val isSheetOpen: Boolean = false,
    val isEditing: Boolean = false,
    val editingCategoryId: Long? = null,

    val nameInput: String = "",
    val selectedIconName: String = "ic_food",
    val selectedColorHex: String = "#FF9800"
)

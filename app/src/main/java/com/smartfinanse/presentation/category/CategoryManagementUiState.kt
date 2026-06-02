package com.smartfinanse.presentation.category

import com.smartfinanse.domain.model.Category

data class CategoryManagementUiState(
    val isLoading: Boolean = true,
    val categories: List<Category> = emptyList(),
    
    // Bottom sheet state for Add/Edit
    val isSheetOpen: Boolean = false,
    val isEditing: Boolean = false,
    val editingCategoryId: Long? = null,
    
    // Form fields
    val nameInput: String = "",
    val selectedIconName: String = "ic_food", // Default icon or file://...
    val selectedColorHex: String = "#FF9800"
)

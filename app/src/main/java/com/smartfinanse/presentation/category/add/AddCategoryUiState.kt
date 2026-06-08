package com.smartfinanse.presentation.category.add

data class AddCategoryUiState(
    val isExpense: Boolean = true,
    val nameInput: String = "",
    val selectedColorHex: String = "#FF9800",
    val selectedIconName: String = "ic_other",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val savedCategoryId: Long? = null,
    val error: String? = null
)

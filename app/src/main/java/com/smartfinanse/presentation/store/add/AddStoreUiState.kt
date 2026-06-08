package com.smartfinanse.presentation.store.add

data class AddStoreUiState(
    val nameInput: String = "",
    val selectedIconName: String = "ShoppingCart",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val savedStoreId: Long? = null,
    val error: String? = null
)

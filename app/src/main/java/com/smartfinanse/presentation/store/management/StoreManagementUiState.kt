package com.smartfinanse.presentation.store.management

import com.smartfinanse.domain.model.Store

data class StoreManagementUiState(
    val stores: List<Store> = emptyList(),
    val filteredStores: List<Store> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val showDeleteConfirmationFor: Store? = null,
    val showEditDialogFor: Store? = null,
    val editNameInput: String = ""
)

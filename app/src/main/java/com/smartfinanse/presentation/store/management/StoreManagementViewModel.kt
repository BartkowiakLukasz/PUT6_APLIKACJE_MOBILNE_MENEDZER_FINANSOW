package com.smartfinanse.presentation.store.management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfinanse.domain.model.Store
import com.smartfinanse.domain.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.smartfinanse.domain.util.capitalizeFirst
import javax.inject.Inject

@HiltViewModel
class StoreManagementViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoreManagementUiState())
    val uiState: StateFlow<StoreManagementUiState> = _uiState.asStateFlow()

    init {
        loadStores()
    }

    private fun loadStores() {
        _uiState.update { it.copy(isLoading = true) }
        storeRepository.getAllStores()
            .onEach { stores ->
                _uiState.update { state ->
                    val filtered = if (state.searchQuery.isBlank()) stores else stores.filter { it.name.contains(state.searchQuery, ignoreCase = true) }
                    state.copy(stores = stores, filteredStores = filtered, isLoading = false) 
                }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            val filtered = if (query.isBlank()) state.stores else state.stores.filter { it.name.contains(query, ignoreCase = true) }
            state.copy(searchQuery = query, filteredStores = filtered)
        }
    }

    fun showDeleteConfirmation(store: Store) {
        _uiState.update { it.copy(showDeleteConfirmationFor = store) }
    }

    fun hideDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirmationFor = null) }
    }

    fun deleteStore() {
        val store = _uiState.value.showDeleteConfirmationFor ?: return
        viewModelScope.launch {
            storeRepository.deleteStore(store.id)
            hideDeleteConfirmation()
        }
    }

    fun showEditDialog(store: Store) {
        _uiState.update { 
            it.copy(
                showEditDialogFor = store,
                editNameInput = store.name.capitalizeFirst()
            ) 
        }
    }

    fun hideEditDialog() {
        _uiState.update { it.copy(showEditDialogFor = null, editNameInput = "") }
    }

    fun onEditNameChanged(newName: String) {
        _uiState.update { it.copy(editNameInput = newName) }
    }

    fun saveEditedStore() {
        val store = _uiState.value.showEditDialogFor ?: return
        val newName = _uiState.value.editNameInput.trim().lowercase()
        if (newName.isBlank()) return

        viewModelScope.launch {
            val updatedStore = store.copy(name = newName)
            storeRepository.updateStore(updatedStore)
            hideEditDialog()
        }
    }
}

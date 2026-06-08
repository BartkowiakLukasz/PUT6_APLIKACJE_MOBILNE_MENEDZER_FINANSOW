package com.smartfinanse.presentation.store.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfinanse.domain.model.Store
import com.smartfinanse.domain.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddStoreViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddStoreUiState())
    val uiState: StateFlow<AddStoreUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(nameInput = name, error = null) }
    }

    fun onIconSelected(iconName: String) {
        _uiState.update { it.copy(selectedIconName = iconName) }
    }

    fun saveStore() {
        val state = _uiState.value
        if (state.nameInput.isBlank()) {
            _uiState.update { it.copy(error = "Nazwa sklepu nie może być pusta") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            try {
                val store = Store(
                    id = 0,
                    name = state.nameInput.trim().lowercase(),
                    iconName = state.selectedIconName
                )
                val newId = storeRepository.insertStore(store)
                _uiState.update { 
                    it.copy(
                        isSaving = false,
                        saveSuccess = true,
                        savedStoreId = newId
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = "Błąd: ${e.localizedMessage}") }
            }
        }
    }
}

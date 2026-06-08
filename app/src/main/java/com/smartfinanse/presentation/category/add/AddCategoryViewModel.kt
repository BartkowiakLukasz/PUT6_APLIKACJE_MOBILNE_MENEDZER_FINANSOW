package com.smartfinanse.presentation.category.add

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfinanse.domain.model.Category
import com.smartfinanse.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddCategoryUiState())
    val uiState: StateFlow<AddCategoryUiState> = _uiState.asStateFlow()

    init {
        val isExpense = savedStateHandle.get<Boolean>("isExpense") ?: true
        _uiState.update { it.copy(isExpense = isExpense) }
    }

    fun updateNameInput(name: String) {
        _uiState.update { it.copy(nameInput = name, error = null) }
    }

    fun updateSelectedColor(colorHex: String) {
        _uiState.update { it.copy(selectedColorHex = colorHex) }
    }

    fun updateSelectedIcon(iconName: String) {
        _uiState.update { it.copy(selectedIconName = iconName) }
    }

    fun onImagePicked(uri: Uri) {
        _uiState.update { it.copy(selectedIconName = uri.toString()) }
    }

    fun saveCategory() {
        val state = _uiState.value
        if (state.nameInput.isBlank()) {
            _uiState.update { it.copy(error = "Nazwa kategorii nie może być pusta") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            try {
                val newCategory = Category(
                    id = 0,
                    name = state.nameInput.trim().lowercase(),
                    iconName = state.selectedIconName,
                    isExpense = state.isExpense,
                    colorHex = state.selectedColorHex
                )
                val newId = categoryRepository.addCategory(newCategory)
                _uiState.update { 
                    it.copy(
                        isSaving = false, 
                        saveSuccess = true,
                        savedCategoryId = newId
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = "Błąd: ${e.localizedMessage}") }
            }
        }
    }
}

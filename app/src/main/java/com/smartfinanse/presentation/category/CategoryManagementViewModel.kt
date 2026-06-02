package com.smartfinanse.presentation.category

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfinanse.domain.model.Category
import com.smartfinanse.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class CategoryManagementViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryManagementUiState())
    val uiState: StateFlow<CategoryManagementUiState> = _uiState.asStateFlow()

    init {
        observeCategories()
    }

    private fun observeCategories() {
        viewModelScope.launch {
            categoryRepository.getCategories(true)
                .catch { /* Handle error */ }
                .collect { expenses ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            categories = expenses
                        )
                    }
                }
        }
    }

    fun openSheetForAdd() {
        _uiState.update {
            it.copy(
                isSheetOpen = true,
                isEditing = false,
                editingCategoryId = null,
                nameInput = "",
                selectedIconName = "ic_food",
                selectedColorHex = "#FF9800"
            )
        }
    }

    fun openSheetForEdit(category: Category) {
        _uiState.update {
            it.copy(
                isSheetOpen = true,
                isEditing = true,
                editingCategoryId = category.id,
                nameInput = category.name,
                selectedIconName = category.iconName,
                selectedColorHex = category.colorHex
            )
        }
    }

    fun closeSheet() {
        _uiState.update { it.copy(isSheetOpen = false) }
    }

    fun updateNameInput(name: String) {
        _uiState.update { it.copy(nameInput = name) }
    }

    fun updateSelectedIcon(iconName: String) {
        _uiState.update { it.copy(selectedIconName = iconName) }
    }

    fun updateSelectedColor(colorHex: String) {
        _uiState.update { it.copy(selectedColorHex = colorHex) }
    }

    fun onImagePicked(uri: Uri) {
        viewModelScope.launch {
            val savedPath = saveImageToInternalStorage(uri)
            if (savedPath != null) {
                _uiState.update { it.copy(selectedIconName = savedPath) }
            }
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = "category_icon_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)
            
            inputStream?.copyTo(outputStream)
            
            inputStream?.close()
            outputStream.close()
            
            "file://${file.absolutePath}"
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveCategory() {
        val state = _uiState.value
        if (state.nameInput.isBlank()) return

        val category = Category(
            id = state.editingCategoryId ?: 0L,
            name = state.nameInput.trim(),
            iconName = state.selectedIconName,
            isExpense = true, // Force all to be expenses
            colorHex = state.selectedColorHex
        )

        viewModelScope.launch {
            if (state.isEditing) {
                categoryRepository.updateCategory(category)
            } else {
                categoryRepository.addCategory(category)
            }
            closeSheet()
        }
    }
}

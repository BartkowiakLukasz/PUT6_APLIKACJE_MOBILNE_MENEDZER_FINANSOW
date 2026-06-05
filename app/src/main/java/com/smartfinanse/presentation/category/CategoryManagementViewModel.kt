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
import kotlinx.coroutines.Job
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
    private var categoriesJob: Job? = null

    init {
        observeCategories()
    }

    fun selectExpenseTab() {
        if (_uiState.value.managingExpenses) return
        _uiState.update { it.copy(managingExpenses = true) }
        observeCategories()
    }

    fun selectIncomeTab() {
        if (!_uiState.value.managingExpenses) return
        _uiState.update { it.copy(managingExpenses = false) }
        observeCategories()
    }

    private fun observeCategories() {
        categoriesJob?.cancel()
        categoriesJob = viewModelScope.launch {
            categoryRepository.getCategories(_uiState.value.managingExpenses)
                .catch { /* Handle error */ }
                .collect { categories ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            categories = categories
                        )
                    }
                }
        }
    }

    fun openSheetForAdd() {
        val isExpense = _uiState.value.managingExpenses
        _uiState.update {
            it.copy(
                isSheetOpen = true,
                isEditing = false,
                editingCategoryId = null,
                nameInput = "",
                selectedIconName = if (isExpense) "ic_food" else "ic_work",
                selectedColorHex = if (isExpense) "#FF9800" else "#1B5E20"
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

        val isExpense = if (state.isEditing) {
            state.categories.find { it.id == state.editingCategoryId }?.isExpense
                ?: state.managingExpenses
        } else {
            state.managingExpenses
        }

        val category = Category(
            id = state.editingCategoryId ?: 0L,
            name = state.nameInput.trim(),
            iconName = state.selectedIconName,
            isExpense = isExpense,
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

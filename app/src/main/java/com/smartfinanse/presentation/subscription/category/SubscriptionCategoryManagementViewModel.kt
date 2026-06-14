package com.smartfinanse.presentation.subscription.category

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfinanse.domain.model.SubscriptionCategory
import com.smartfinanse.domain.repository.SubscriptionCategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import android.graphics.Color

@HiltViewModel
class SubscriptionCategoryManagementViewModel @Inject constructor(
    private val categoryRepository: SubscriptionCategoryRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionCategoryManagementUiState())
    val uiState: StateFlow<SubscriptionCategoryManagementUiState> = _uiState.asStateFlow()
    private var categoriesJob: Job? = null

    init {
        observeCategories()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            val filtered = if (query.isBlank()) state.categories else state.categories.filter { it.name.contains(query, ignoreCase = true) }
            state.copy(searchQuery = query, filteredCategories = filtered)
        }
    }

    private fun observeCategories() {
        categoriesJob?.cancel()
        categoriesJob = viewModelScope.launch {
            categoryRepository.getAllCategories()
                .catch { /* Handle error */ }
                .collect { categories ->
                    _uiState.update { state ->
                        val filtered = if (state.searchQuery.isBlank()) categories else categories.filter { it.name.contains(state.searchQuery, ignoreCase = true) }
                        state.copy(
                            isLoading = false,
                            categories = categories,
                            filteredCategories = filtered
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
                selectedIconName = "ic_entertainment",
                selectedColorHex = "#E50914"
            )
        }
    }

    fun openSheetForEdit(category: SubscriptionCategory) {
        // Convert category.color (Long) to Hex string
        val colorHex = String.format("#%06X", (0xFFFFFF and category.color.toInt()))
        
        _uiState.update {
            it.copy(
                isSheetOpen = true,
                isEditing = true,
                editingCategoryId = category.id,
                nameInput = category.name,
                selectedIconName = category.iconId ?: "ic_other",
                selectedColorHex = colorHex
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
            val fileName = "sub_category_icon_${System.currentTimeMillis()}.jpg"
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

        val colorLong = try {
            Color.parseColor(state.selectedColorHex).toLong()
        } catch (e: Exception) {
            0L
        }

        val category = SubscriptionCategory(
            id = state.editingCategoryId ?: 0L,
            name = state.nameInput.trim(),
            color = colorLong,
            iconId = state.selectedIconName
        )

        viewModelScope.launch {
            if (state.isEditing) {
                // Update doesn't exist in SubscriptionCategoryRepository. We can use insertCategory since it has OnConflictStrategy.REPLACE
                categoryRepository.insertCategory(category)
            } else {
                categoryRepository.insertCategory(category)
            }
            closeSheet()
        }
    }

    fun showDeleteConfirmation(category: SubscriptionCategory) {
        _uiState.update { it.copy(showDeleteConfirmationFor = category) }
    }

    fun hideDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirmationFor = null) }
    }

    fun deleteCategory() {
        val category = _uiState.value.showDeleteConfirmationFor ?: return
        viewModelScope.launch {
            categoryRepository.deleteCategory(category.id)
            hideDeleteConfirmation()
        }
    }
}

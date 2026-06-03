package com.smartfinanse.presentation.transaction.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfinanse.domain.model.Category
import com.smartfinanse.domain.model.Transaction
import com.smartfinanse.domain.repository.CategoryRepository
import com.smartfinanse.domain.usecase.AddTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.lifecycle.SavedStateHandle
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val categoryRepository: CategoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    init {
        val amount = savedStateHandle.get<String>("amount") ?: ""
        val description = savedStateHandle.get<String>("description") ?: ""
        val dateStr = savedStateHandle.get<String>("date")
        
        var dateMillis = System.currentTimeMillis()
        if (!dateStr.isNullOrBlank() && dateStr != "null") {
            try {
                val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                dateMillis = format.parse(dateStr)?.time ?: dateMillis
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        val categoryIdStr = savedStateHandle.get<String>("categoryId")
        val categoryId = if (categoryIdStr != "null") categoryIdStr?.toLongOrNull() else null

        val isCashStr = savedStateHandle.get<String>("isCash")
        val isCashParam = if (isCashStr != "null") isCashStr?.toBooleanStrictOrNull() else null

        _uiState.update { 
            it.copy(
                amount = amount,
                description = description,
                date = dateMillis,
                selectedCategoryId = categoryId,
                isCash = isCashParam ?: it.isCash
            )
        }

        loadCategories()
    }

    private fun loadCategories() {
        categoryRepository.getCategories(isExpense = _uiState.value.isExpense)
            .onEach { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
            .launchIn(viewModelScope)
    }

    fun onAmountChange(amount: String) {
        _uiState.update { it.copy(amount = amount, amountError = null, globalError = null) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onDateChange(date: Long) {
        _uiState.update { it.copy(date = date) }
    }

    fun onIsCashChange(isCash: Boolean) {
        _uiState.update { it.copy(isCash = isCash) }
    }

    fun onIsRecurringChange(isRecurring: Boolean) {
        _uiState.update { it.copy(isRecurring = isRecurring) }
    }

    fun onIsExpenseChange(isExpense: Boolean) {
        _uiState.update { it.copy(isExpense = isExpense) }
        loadCategories()
    }

    fun onCategorySelected(categoryId: Long) {
        _uiState.update { it.copy(selectedCategoryId = categoryId, categoryError = null) }
    }

    fun showAddCategoryDialog(show: Boolean) {
        _uiState.update { it.copy(showAddCategoryDialog = show) }
    }

    fun addNewCategory(name: String) {
        if (name.isBlank()) return
        
        // Wybierzmy jakiś ładny losowy kolor z palety, by wyglądał dobrze na nowej kategorii
        val availableColors = listOf("#F44336", "#673AB7", "#3F51B5", "#009688", "#795548", "#607D8B", "#FFC107")
        val randomColor = availableColors.random()

        viewModelScope.launch {
            val newCategory = Category(
                id = 0,
                name = name.trim(),
                iconName = "ic_category_other",
                isExpense = _uiState.value.isExpense,
                colorHex = randomColor
            )
            val newId = categoryRepository.addCategory(newCategory)
            _uiState.update { 
                it.copy(
                    selectedCategoryId = newId,
                    showAddCategoryDialog = false,
                    categoryError = null
                ) 
            }
        }
    }

    fun clearGlobalError() {
        _uiState.update { it.copy(globalError = null) }
    }

    fun saveTransaction() {
        val state = _uiState.value
        
        var hasError = false
        var amountError: String? = null
        var categoryError: String? = null

        val amountParsed = state.amount.replace(",", ".").toDoubleOrNull()
        if (amountParsed == null || amountParsed <= 0) {
            amountError = "Wprowadź poprawną kwotę większą od 0."
            hasError = true
        }

        if (state.selectedCategoryId == null) {
            categoryError = "Wybierz kategorię wydatku."
            hasError = true
        }

        if (hasError) {
            _uiState.update { it.copy(amountError = amountError, categoryError = categoryError) }
            return
        }

        val amountInGroschen = (amountParsed!! * 100).toLong()

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, globalError = null) }
            
            try {
                val transaction = Transaction(
                    id = 0,
                    categoryId = state.selectedCategoryId,
                    amount = amountInGroschen,
                    description = state.description.ifBlank { if (state.isExpense) "Wydatek" else "Przychód" },
                    date = state.date,
                    isCash = state.isCash,
                    isRecurring = state.isRecurring,
                    location = null,
                    receiptImageUri = null
                )

                addTransactionUseCase(transaction)
                
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, globalError = "Błąd podczas zapisywania: ${e.localizedMessage}") }
            }
        }
    }
}

package com.smartfinanse.presentation.transaction.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfinanse.domain.model.Category
import com.smartfinanse.domain.model.Transaction
import com.smartfinanse.domain.repository.CategoryRepository
import com.smartfinanse.domain.repository.StoreRepository
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
    private val storeRepository: StoreRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    init {
        val transactionType = savedStateHandle.get<String>("transactionType")
        val isSubscription = transactionType == "subscription"
        val isExpense = transactionType != "income" // Zatem subskrypcja to też expense

        val amount = savedStateHandle.get<String>("amount") ?: ""
        val description = savedStateHandle.get<String>("description") ?: ""
        val storeName = savedStateHandle.get<String>("storeName") ?: ""
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
        
        val storeIdStr = savedStateHandle.get<String>("storeId")
        val storeId = if (storeIdStr != "null") storeIdStr?.toLongOrNull() else null

        val isFallbackCategoryStr = savedStateHandle.get<String>("isFallbackCategory")
        val isFallbackCategory = isFallbackCategoryStr == "true"
        val categoryErrorMsg = if (isFallbackCategory) "Nie udało się sklasyfikować kategorii, wybrano 'Inne'." else null

        _uiState.update {
            it.copy(
                isExpense = isExpense,
                isSubscription = isSubscription,
                isRecurring = isSubscription || it.isRecurring, // always true if subscription
                amount = amount,
                description = description,
                storeSearchQuery = storeName,
                selectedStoreId = storeId,
                date = dateMillis,
                selectedCategoryId = categoryId,
                isCash = isCashParam ?: it.isCash,
                categoryError = categoryErrorMsg
            )
        }

        loadCategories()

        savedStateHandle.getStateFlow<Long?>("newCategoryId", null)
            .onEach { newId ->
                if (newId != null) {
                    onCategorySelected(newId)
                    savedStateHandle.remove<Long>("newCategoryId")
                }
            }
            .launchIn(viewModelScope)

        savedStateHandle.getStateFlow<Long?>("newStoreId", null)
            .onEach { newId ->
                if (newId != null) {
                    onStoreSelected(newId)
                    savedStateHandle.remove<Long>("newStoreId")
                }
            }
            .launchIn(viewModelScope)
            
        loadRecentStores()
    }

    private fun loadCategories() {
        categoryRepository.getCategories(isExpense = _uiState.value.isExpense)
            .onEach { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
            .launchIn(viewModelScope)
    }

    private fun loadRecentStores() {
        storeRepository.getRecentStores()
            .onEach { stores ->
                _uiState.update { it.copy(recentStores = stores) }
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

    fun onCategorySelected(categoryId: Long) {
        _uiState.update { it.copy(selectedCategoryId = categoryId, categoryError = null) }
    }

    fun onCategorySearchQueryChanged(query: String) {
        _uiState.update { it.copy(categorySearchQuery = query) }
    }

    fun onStoreSelected(storeId: Long?) {
        _uiState.update { it.copy(selectedStoreId = storeId) }
    }

    fun onStoreSearchQueryChanged(query: String) {
        _uiState.update { it.copy(storeSearchQuery = query) }
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
            categoryError = "Wybierz kategorię."
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
                    description = state.description.ifBlank { if (state.isExpense) "wydatek" else "przychód" }.lowercase(),
                    date = state.date,
                    isCash = state.isCash,
                    isRecurring = state.isRecurring,
                    location = null, // Przenieśliśmy na Store
                    receiptImageUri = null,
                    storeId = state.selectedStoreId
                )

                addTransactionUseCase(transaction)
                
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, globalError = "Błąd podczas zapisywania: ${e.localizedMessage}") }
            }
        }
    }
}

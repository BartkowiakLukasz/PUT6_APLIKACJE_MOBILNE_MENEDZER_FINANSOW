package com.smartfinanse.presentation.subscription.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfinanse.domain.model.BillingCycle
import com.smartfinanse.domain.model.Subscription
import com.smartfinanse.domain.repository.SubscriptionCategoryRepository
import com.smartfinanse.domain.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddSubscriptionViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val categoryRepository: SubscriptionCategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddSubscriptionUiState())
    val uiState: StateFlow<AddSubscriptionUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories()
                .catch { /* Handle error */ }
                .collect { categories ->
                    _uiState.update { it.copy(categories = categories) }
                }
        }
    }

    fun onServiceNameChange(name: String) {
        _uiState.update { it.copy(serviceName = name, nameError = null) }
    }

    fun onAmountChange(amount: String) {
        _uiState.update { it.copy(amount = amount, amountError = null) }
    }

    fun onDateChange(date: Long) {
        _uiState.update { it.copy(startDate = date) }
    }

    fun onCycleChange(isMonthly: Boolean) {
        _uiState.update { it.copy(isMonthly = isMonthly) }
    }

    fun onCategorySelected(categoryId: Long) {
        _uiState.update { it.copy(selectedCategoryId = categoryId, categoryError = null) }
    }

    fun onCategorySearchQueryChanged(query: String) {
        _uiState.update { it.copy(categorySearchQuery = query) }
    }

    fun saveSubscription() {
        val state = _uiState.value
        
        var isValid = true
        var nameError: String? = null
        var amountError: String? = null
        var categoryError: String? = null

        if (state.serviceName.isBlank()) {
            isValid = false
            nameError = "Nazwa usługi jest wymagana"
        }

        val parsedAmount = state.amount.replace(",", ".").toDoubleOrNull()
        if (parsedAmount == null || parsedAmount <= 0) {
            isValid = false
            amountError = "Wprowadź poprawną kwotę"
        }

        if (state.selectedCategoryId == null) {
            isValid = false
            categoryError = "Wybierz kategorię"
        }

        if (!isValid) {
            _uiState.update { 
                it.copy(
                    nameError = nameError,
                    amountError = amountError,
                    categoryError = categoryError
                )
            }
            return
        }

        _uiState.update { it.copy(isSaving = true, globalError = null) }

        viewModelScope.launch {
            try {
                val amountInGrosze = (parsedAmount!! * 100).toLong()
                
                val subscription = Subscription(
                    id = 0,
                    serviceName = state.serviceName,
                    amount = amountInGrosze,
                    startDate = state.startDate,
                    billingCycle = if (state.isMonthly) BillingCycle.MONTHLY else BillingCycle.YEARLY,
                    categoryId = state.selectedCategoryId
                )
                
                subscriptionRepository.insertSubscription(subscription)
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isSaving = false, 
                        globalError = e.message ?: "Wystąpił nieoczekiwany błąd"
                    )
                }
            }
        }
    }
}

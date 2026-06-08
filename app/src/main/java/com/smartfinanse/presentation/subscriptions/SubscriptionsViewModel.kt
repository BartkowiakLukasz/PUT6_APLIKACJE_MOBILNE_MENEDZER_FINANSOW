package com.smartfinanse.presentation.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfinanse.domain.usecase.GetSubscriptionsUseCase
import com.smartfinanse.presentation.common.DateFormatter
import com.smartfinanse.presentation.common.MoneyFormatter
import com.smartfinanse.domain.util.capitalizeFirst
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.catch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val getSubscriptionsUseCase: GetSubscriptionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionsUiState(isLoading = true))
    val uiState: StateFlow<SubscriptionsUiState> = _uiState.asStateFlow()

    init {
        loadSubscriptions()
    }

    private fun loadSubscriptions() {
        getSubscriptionsUseCase()
            .onEach { items ->
                val today = LocalDate.now()
                val zoneId = ZoneId.systemDefault()

                // Calculate total monthly cost (assuming all are monthly for the total, or just summing the amounts)
                // For a more accurate total we would convert periods to monthly equivalent, but let's just sum for now.
                var totalExpenses = 0L

                val uiItems = items.map { item ->
                    val sub = item.subscription
                    
                    totalExpenses += sub.amount

                    val renewalDate = Instant.ofEpochMilli(item.nextRenewalDateMillis).atZone(zoneId).toLocalDate()
                    val daysUntil = ChronoUnit.DAYS.between(today, renewalDate).toInt()

                    val dateText = when (daysUntil) {
                        0 -> "Dziś"
                        1 -> "Jutro"
                        else -> "Za $daysUntil dni (${DateFormatter.format(item.nextRenewalDateMillis)})"
                    }

                    SubscriptionItemUi(
                        id = sub.id,
                        title = sub.serviceName.capitalizeFirst(),
                        categoryName = sub.categoryName?.capitalizeFirst(),
                        categoryId = sub.categoryId,
                        amountFormatted = MoneyFormatter.format(sub.amount, true), // all subscriptions are expenses
                        nextRenewalDateFormatted = dateText,
                        daysUntilRenewal = daysUntil,
                        isExpense = true
                    )
                }

                _uiState.update { 
                    it.copy(
                        subscriptions = uiItems,
                        totalMonthlyCostFormatted = MoneyFormatter.format(totalExpenses, true),
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
            .catch { e ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Nieoczekiwany błąd"
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}

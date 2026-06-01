package com.smartfinanse.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfinanse.domain.model.TransactionWithCategory
import com.smartfinanse.domain.usecase.GetAllTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getAllTransactionsUseCase: GetAllTransactionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var allTransactionsList: List<TransactionWithCategory> = emptyList()

    init {
        observeTransactions()
    }

    fun setTimeFilter(filter: TimeFilter) {
        if (filter != TimeFilter.CUSTOM) {
            _uiState.update { it.copy(selectedFilter = filter, customStartDate = null, customEndDate = null) }
            processTransactions()
        } else {
            _uiState.update { it.copy(selectedFilter = filter) }
        }
    }

    fun setCustomTimeFilter(startDate: Long, endDate: Long) {
        _uiState.update { 
            it.copy(
                selectedFilter = TimeFilter.CUSTOM,
                customStartDate = startDate,
                customEndDate = endDate
            ) 
        }
        processTransactions()
    }

    private fun observeTransactions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            getAllTransactionsUseCase()
                .catch { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
                .collect { allTransactions ->
                    allTransactionsList = allTransactions
                    processTransactions()
                }
        }
    }

    private fun processTransactions() {
        val filtered = filterByTime(allTransactionsList, _uiState.value.selectedFilter)
        
        // Tylko wydatki (isExpense = true) na wykresie
        val expenses = filtered.filter { it.category?.isExpense == true }
        
        val totalExpenses = expenses.sumOf { it.transaction.amount }
        
        val grouped = expenses.groupBy { it.category?.id ?: -1L }
        
        val breakdown = grouped.map { (catId, txs) ->
            val firstCat = txs.firstOrNull()?.category
            val catTotal = txs.sumOf { it.transaction.amount }
            CategoryBreakdownItem(
                categoryId = catId,
                name = firstCat?.name ?: "Brak kategorii",
                colorHex = firstCat?.colorHex ?: "#888888",
                totalAmount = catTotal,
                percentage = if (totalExpenses > 0) catTotal.toFloat() / totalExpenses.toFloat() else 0f
            )
        }.sortedByDescending { it.totalAmount }
        
        _uiState.update { 
            it.copy(
                isLoading = false,
                totalAmount = totalExpenses,
                categoryBreakdown = breakdown,
                errorMessage = null
            ) 
        }
    }

    private fun filterByTime(transactions: List<TransactionWithCategory>, filter: TimeFilter): List<TransactionWithCategory> {
        val startOfToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        return transactions.filter { txWithCat ->
            val txDate = txWithCat.transaction.date
            when (filter) {
                TimeFilter.DAY -> txDate >= startOfToday
                TimeFilter.WEEK -> {
                    val startOfWeek = Calendar.getInstance().apply {
                        firstDayOfWeek = Calendar.MONDAY
                        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                    txDate >= startOfWeek
                }
                TimeFilter.MONTH -> {
                    val startOfMonth = Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_MONTH, 1)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                    txDate >= startOfMonth
                }
                TimeFilter.YEAR -> {
                    val startOfYear = Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_YEAR, 1)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                    txDate >= startOfYear
                }
                TimeFilter.CUSTOM -> {
                    val start = _uiState.value.customStartDate
                    val end = _uiState.value.customEndDate
                    if (start != null && end != null) {
                        // Include the entire end day
                        val endOfDay = Calendar.getInstance().apply {
                            timeInMillis = end
                            set(Calendar.HOUR_OF_DAY, 23)
                            set(Calendar.MINUTE, 59)
                            set(Calendar.SECOND, 59)
                            set(Calendar.MILLISECOND, 999)
                        }.timeInMillis
                        txDate in start..endOfDay
                    } else {
                        true // No range selected yet, show all or could show none
                    }
                }
            }
        }
    }
}

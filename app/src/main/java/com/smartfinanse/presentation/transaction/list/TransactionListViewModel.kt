package com.smartfinanse.presentation.transaction.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfinanse.domain.model.TransactionWithCategory
import com.smartfinanse.domain.repository.CategoryRepository
import com.smartfinanse.domain.usecase.GetAllTransactionsUseCase
import com.smartfinanse.presentation.dashboard.TimeFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val getAllTransactionsUseCase: GetAllTransactionsUseCase,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionListUiState(isLoading = true))
    val uiState: StateFlow<TransactionListUiState> = _uiState.asStateFlow()

    private val selectedCategoryFlow = MutableStateFlow<Long?>(null)
    private val timeFilterFlow = MutableStateFlow(TimeFilter.MONTH)
    private val customStartDateFlow = MutableStateFlow<Long?>(null)
    private val customEndDateFlow = MutableStateFlow<Long?>(null)
    private val categorySearchQueryFlow = MutableStateFlow("")

    init {
        observeData()
    }

    fun onCategorySelected(categoryId: Long?) {
        selectedCategoryFlow.value = categoryId
    }

    fun onCategorySearchQueryChanged(query: String) {
        categorySearchQueryFlow.value = query
    }

    fun setTimeFilter(filter: TimeFilter) {
        if (filter != TimeFilter.CUSTOM) {
            timeFilterFlow.value = filter
            customStartDateFlow.value = null
            customEndDateFlow.value = null
        } else {
            timeFilterFlow.value = filter
        }
    }

    fun setCustomTimeFilter(startDate: Long, endDate: Long) {
        timeFilterFlow.value = TimeFilter.CUSTOM
        customStartDateFlow.value = startDate
        customEndDateFlow.value = endDate
    }

    private data class FilterState(
        val categoryId: Long?,
        val timeFilter: TimeFilter,
        val startDate: Long?,
        val endDate: Long?,
        val searchQuery: String
    )

    private fun observeData() {
        viewModelScope.launch {
            val allCategoriesFlow = combine(
                categoryRepository.getCategories(true),
                categoryRepository.getCategories(false)
            ) { exp, inc -> exp + inc }

            val filterStateFlow = combine(
                selectedCategoryFlow,
                timeFilterFlow,
                customStartDateFlow,
                customEndDateFlow,
                categorySearchQueryFlow
            ) { cat, time, start, end, query ->
                FilterState(cat, time, start, end, query)
            }

            combine(
                getAllTransactionsUseCase(),
                allCategoriesFlow,
                filterStateFlow
            ) { transactions, categories, filters ->
                // Filtrowanie transakcji
                val timeFiltered = filterByTime(transactions, filters.timeFilter, filters.startDate, filters.endDate)
                val uiTransactions = timeFiltered.map { it.toUi() }
                
                val filteredTxs = if (filters.categoryId != null) {
                    uiTransactions.filter { it.categoryId == filters.categoryId }
                } else {
                    uiTransactions
                }

                val grouped = filteredTxs.groupBy { it.dateFormatted }

                // Filtrowanie kategorii po nazwie
                val searchQ = filters.searchQuery.trim().lowercase()
                val displayCategories = if (searchQ.isNotEmpty()) {
                    categories.filter { it.name.lowercase().contains(searchQ) }
                } else {
                    categories
                }

                TransactionListUiState(
                    groupedTransactions = grouped,
                    categories = displayCategories,
                    categorySearchQuery = filters.searchQuery,
                    selectedCategoryId = filters.categoryId,
                    selectedTimeFilter = filters.timeFilter,
                    customStartDate = filters.startDate,
                    customEndDate = filters.endDate,
                    isLoading = false,
                    errorMessage = null
                )
            }
            .catch { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
            }
            .collect { newState ->
                _uiState.value = newState
            }
        }
    }

    private fun filterByTime(transactions: List<TransactionWithCategory>, filter: TimeFilter, customStart: Long?, customEnd: Long?): List<TransactionWithCategory> {
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
                TimeFilter.ALL -> true
                TimeFilter.CUSTOM -> {
                    if (customStart != null && customEnd != null) {
                        val endOfDay = Calendar.getInstance().apply {
                            timeInMillis = customEnd
                            set(Calendar.HOUR_OF_DAY, 23)
                            set(Calendar.MINUTE, 59)
                            set(Calendar.SECOND, 59)
                            set(Calendar.MILLISECOND, 999)
                        }.timeInMillis
                        txDate in customStart..endOfDay
                    } else {
                        true
                    }
                }
            }
        }
    }
}

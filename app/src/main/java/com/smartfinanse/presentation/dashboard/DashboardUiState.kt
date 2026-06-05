package com.smartfinanse.presentation.dashboard

enum class TimeFilter(val label: String) {
    DAY("Dzień"),
    WEEK("Tydzień"),
    MONTH("Miesiąc"),
    YEAR("Rok"),
    ALL("Cały okres"),
    CUSTOM("Zakres")
}

enum class DashboardContentFilter {
    EXPENSES_ONLY,
    BOTH,
    INCOME_ONLY
}

data class CategoryBreakdownItem(
    val categoryId: Long,
    val name: String,
    val colorHex: String,
    val totalAmount: Long,
    val percentage: Float
)

data class DashboardUiState(
    val isLoading: Boolean = true,
    val selectedFilter: TimeFilter = TimeFilter.MONTH,
    val customStartDate: Long? = null,
    val customEndDate: Long? = null,
    val contentFilter: DashboardContentFilter = DashboardContentFilter.BOTH,
    val netBalance: Long = 0L,
    val totalIncome: Long = 0L,
    val totalExpenses: Long = 0L,
    val cashExpensePercent: Int = 0,
    val cardExpensePercent: Int = 0,
    val expenseBreakdown: List<CategoryBreakdownItem> = emptyList(),
    val incomeBreakdown: List<CategoryBreakdownItem> = emptyList(),
    val errorMessage: String? = null
)

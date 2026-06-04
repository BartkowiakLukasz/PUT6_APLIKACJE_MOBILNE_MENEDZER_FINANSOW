package com.smartfinanse.presentation.dashboard

enum class TimeFilter(val label: String) {
    DAY("Dzień"),
    WEEK("Tydzień"),
    MONTH("Miesiąc"),
    YEAR("Rok"),
    ALL("Cały okres"),
    CUSTOM("Zakres")
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
    /** Net balance in period: income − expenses (grosze) */
    val netBalance: Long = 0L,
    val totalIncome: Long = 0L,
    val totalExpenses: Long = 0L,
    val cashExpensePercent: Int = 0,
    val cardExpensePercent: Int = 0,
    val totalAmount: Long = 0L,
    val categoryBreakdown: List<CategoryBreakdownItem> = emptyList(),
    val errorMessage: String? = null
)

package com.smartfinanse.presentation.dashboard

enum class TimeFilter(val label: String) {
    DAY("Dzień"),
    WEEK("Tydzień"),
    MONTH("Miesiąc"),
    YEAR("Rok"),
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
    val totalAmount: Long = 0L,
    val categoryBreakdown: List<CategoryBreakdownItem> = emptyList(),
    val errorMessage: String? = null
)

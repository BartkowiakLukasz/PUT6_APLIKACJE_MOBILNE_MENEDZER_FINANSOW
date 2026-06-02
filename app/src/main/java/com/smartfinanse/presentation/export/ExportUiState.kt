package com.smartfinanse.presentation.export

import com.smartfinanse.domain.model.Category

data class ExportUiState(
    val categories: List<Category> = emptyList(),
    val selectedCategories: Set<Long> = emptySet(), // category IDs to export
    val startDate: Long? = null,
    val endDate: Long? = null,
    val selectedTimeFilter: com.smartfinanse.presentation.dashboard.TimeFilter = com.smartfinanse.presentation.dashboard.TimeFilter.ALL,
    val exportCashOnly: Boolean = false,
    val exportCardOnly: Boolean = false,
    
    val isExporting: Boolean = false,
    val isImporting: Boolean = false
)

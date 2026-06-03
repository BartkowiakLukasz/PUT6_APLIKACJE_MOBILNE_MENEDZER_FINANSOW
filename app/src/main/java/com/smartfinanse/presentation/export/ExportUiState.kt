package com.smartfinanse.presentation.export

import android.net.Uri
import com.smartfinanse.domain.model.Category

data class ExportSuccess(val uri: Uri, val format: String, val message: String)
data class ImportSuccess(val importedCount: Int, val duplicatesSkipped: Int, val message: String)

enum class ErrorAction { RETRY, HELP }
data class OperationError(val reason: String, val action: ErrorAction?)

data class ExportUiState(
    val categories: List<Category> = emptyList(),
    val selectedCategories: Set<Long> = emptySet(), // category IDs to export
    val startDate: Long? = null,
    val endDate: Long? = null,
    val selectedTimeFilter: com.smartfinanse.presentation.dashboard.TimeFilter = com.smartfinanse.presentation.dashboard.TimeFilter.ALL,
    val exportCashOnly: Boolean = false,
    val exportCardOnly: Boolean = false,
    
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,

    val exportSuccess: ExportSuccess? = null,
    val importSuccess: ImportSuccess? = null,
    val error: OperationError? = null,
    
    val showOverwriteDialog: Boolean = false,
    val pendingImportUri: Uri? = null
)

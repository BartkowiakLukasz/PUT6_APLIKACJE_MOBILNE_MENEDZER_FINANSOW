package com.smartfinanse.presentation.export

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfinanse.data.export.ExportManager
import com.smartfinanse.data.export.ImportManager
import com.smartfinanse.data.local.dao.CategoryDao
import com.smartfinanse.data.local.dao.TransactionDao
import com.smartfinanse.data.local.entity.TransactionEntity
import com.smartfinanse.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExportUiState())
    val uiState: StateFlow<ExportUiState> = _uiState.asStateFlow()

    private val exportManager = ExportManager(context)
    private val importManager = ImportManager(context)

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getCategories(true).collect { cats ->
                _uiState.update { it.copy(categories = cats) }
            }
        }
    }

    fun toggleCategorySelection(categoryId: Long) {
        _uiState.update { state ->
            val newSelection = if (state.selectedCategories.contains(categoryId)) {
                state.selectedCategories - categoryId
            } else {
                state.selectedCategories + categoryId
            }
            state.copy(selectedCategories = newSelection)
        }
    }

    fun setCustomTimeFilter(start: Long, end: Long) {
        _uiState.update { 
            it.copy(
                selectedTimeFilter = com.smartfinanse.presentation.dashboard.TimeFilter.CUSTOM,
                startDate = start, 
                endDate = end 
            ) 
        }
    }

    fun setTimeFilter(filter: com.smartfinanse.presentation.dashboard.TimeFilter) {
        if (filter == com.smartfinanse.presentation.dashboard.TimeFilter.CUSTOM) return

        val calendar = java.util.Calendar.getInstance()
        val end = calendar.timeInMillis
        var start: Long? = null

        when (filter) {
            com.smartfinanse.presentation.dashboard.TimeFilter.WEEK -> {
                calendar.add(java.util.Calendar.DAY_OF_YEAR, -7)
                start = calendar.timeInMillis
            }
            com.smartfinanse.presentation.dashboard.TimeFilter.MONTH -> {
                calendar.add(java.util.Calendar.MONTH, -1)
                start = calendar.timeInMillis
            }
            com.smartfinanse.presentation.dashboard.TimeFilter.YEAR -> {
                calendar.add(java.util.Calendar.YEAR, -1)
                start = calendar.timeInMillis
            }
            else -> {}
        }

        _uiState.update {
            it.copy(
                selectedTimeFilter = filter,
                startDate = start,
                endDate = if (start != null) end else null
            )
        }
    }

    fun toggleCashOnly(value: Boolean) {
        _uiState.update { it.copy(exportCashOnly = value, exportCardOnly = if (value) false else it.exportCardOnly) }
    }

    fun toggleCardOnly(value: Boolean) {
        _uiState.update { it.copy(exportCardOnly = value, exportCashOnly = if (value) false else it.exportCashOnly) }
    }

    fun generateCsv() {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true, error = null, exportSuccess = null) }
            try {
                val state = _uiState.value
                val transactionsFlow = if (state.startDate != null && state.endDate != null) {
                    transactionDao.getTransactionsBetweenDates(state.startDate, state.endDate)
                } else {
                    transactionDao.getAllWithDetails()
                }

                transactionsFlow.collect { allTrans ->
                    var filtered = allTrans
                    if (state.selectedCategories.isNotEmpty()) {
                        filtered = filtered.filter { t ->
                            t.category?.id?.let { id -> state.selectedCategories.contains(id) } == true
                        }
                    }
                    if (state.exportCashOnly) {
                        filtered = filtered.filter { it.transaction.isCash }
                    } else if (state.exportCardOnly) {
                        filtered = filtered.filter { !it.transaction.isCash }
                    }

                    val uri = withContext(Dispatchers.IO) {
                        exportManager.exportToCsv(filtered)
                    }

                    _uiState.update { 
                        it.copy(
                            isExporting = false, 
                            exportSuccess = ExportSuccess(uri, "CSV", "Dane zostały wyeksportowane")
                        ) 
                    }
                    throw kotlinx.coroutines.CancellationException("Stop collecting")
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
                // To jest oczekiwane po pomyślnym zebraniu pierwszego elementu
            } catch (e: Exception) {
                handleError(e)
                _uiState.update { it.copy(isExporting = false) }
            }
        }
    }

    fun generateJson() {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true, error = null, exportSuccess = null) }
            try {
                val categories = withContext(Dispatchers.IO) { categoryDao.getAllCategoriesRaw() }
                val transactions = withContext(Dispatchers.IO) { transactionDao.getAllTransactionsRaw() }

                val uri = withContext(Dispatchers.IO) {
                    exportManager.exportToJson(categories, transactions)
                }

                _uiState.update { 
                    it.copy(
                        isExporting = false, 
                        exportSuccess = ExportSuccess(uri, "JSON", "Dane zostały wyeksportowane")
                    ) 
                }
            } catch (e: Exception) {
                handleError(e)
                _uiState.update { it.copy(isExporting = false) }
            }
        }
    }

    fun prepareRestoreFromJson(uri: Uri) {
        _uiState.update { it.copy(showOverwriteDialog = true, pendingImportUri = uri) }
    }

    fun confirmRestoreFromJson() {
        val uri = _uiState.value.pendingImportUri ?: return
        _uiState.update { it.copy(showOverwriteDialog = false, pendingImportUri = null) }
        restoreFromJson(uri)
    }

    fun cancelRestoreFromJson() {
        _uiState.update { it.copy(showOverwriteDialog = false, pendingImportUri = null) }
    }

    private fun restoreFromJson(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isImporting = true, error = null, importSuccess = null) }
            try {
                val backupData = importManager.parseJsonBackup(uri)
                
                transactionDao.deleteAll()
                categoryDao.deleteAll()

                categoryDao.insertAll(backupData.categories)
                backupData.transactions.forEach { t ->
                    transactionDao.insertTransaction(t)
                }
                
                _uiState.update { 
                    it.copy(
                        isImporting = false,
                        importSuccess = ImportSuccess(
                            importedCount = backupData.transactions.size,
                            duplicatesSkipped = 0,
                            message = "Pomyślnie zaimportowano bazę"
                        )
                    ) 
                }
            } catch (e: Exception) {
                handleError(e)
                _uiState.update { it.copy(isImporting = false) }
            }
        }
    }

    fun mergeFromCsv(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isImporting = true, error = null, importSuccess = null) }
            try {
                val csvData = importManager.parseCsv(uri)
                val existingRaw = transactionDao.getAllTransactionsRaw()
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val categories = categoryDao.getAllCategoriesRaw()
                
                var imported = 0
                var duplicates = 0

                csvData.forEach { row ->
                    val amount = (row.amountStr.replace(",", ".").toDouble() * 100).toLong()
                    val date = format.parse(row.dateStr)?.time ?: 0L
                    
                    val cat = categories.find { it.name.equals(row.categoryName, ignoreCase = true) }
                    
                    val isDuplicate = existingRaw.any { ext ->
                        ext.amount == amount && 
                        ext.description == row.description &&
                        ext.date == date &&
                        ext.isCash == row.isCash
                    }

                    if (!isDuplicate) {
                        val newT = TransactionEntity(
                            categoryId = cat?.id,
                            amount = amount,
                            description = row.description,
                            date = date,
                            isCash = row.isCash,
                            location = null,
                            receiptImageUri = null
                        )
                        transactionDao.insertTransaction(newT)
                        imported++
                    } else {
                        duplicates++
                    }
                }

                _uiState.update { 
                    it.copy(
                        isImporting = false,
                        importSuccess = ImportSuccess(
                            importedCount = imported,
                            duplicatesSkipped = duplicates,
                            message = "Pomyślnie zaimportowano transakcje"
                        )
                    ) 
                }
            } catch (e: Exception) {
                handleError(e)
                _uiState.update { it.copy(isImporting = false) }
            }
        }
    }

    private fun handleError(e: Exception) {
        e.printStackTrace()
        val reason = when (e) {
            is SecurityException -> "Brak uprawnień do pliku."
            is IOException -> "Błąd odczytu/zapisu pliku. Brak miejsca lub uprawnień."
            is IllegalArgumentException -> e.message ?: "Nieprawidłowy format pliku."
            is com.google.gson.JsonSyntaxException -> "Plik nie jest poprawnym plikiem JSON z danymi aplikacji."
            else -> "Wystąpił nieoczekiwany błąd podczas operacji."
        }
        val action = when (e) {
            is IllegalArgumentException, is com.google.gson.JsonSyntaxException -> ErrorAction.HELP
            else -> ErrorAction.RETRY
        }
        _uiState.update { it.copy(error = OperationError(reason, action)) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(exportSuccess = null, importSuccess = null, error = null) }
    }
    
}

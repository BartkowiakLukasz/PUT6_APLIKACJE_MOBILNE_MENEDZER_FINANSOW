package com.smartfinanse.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfinanse.data.export.ExportManager
import com.smartfinanse.domain.repository.AppTheme
import com.smartfinanse.domain.repository.Currency
import com.smartfinanse.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import com.smartfinanse.data.local.dao.TransactionDao
import com.smartfinanse.data.local.dao.CategoryDao

data class SettingsUiState(
    val selectedTheme: AppTheme = AppTheme.SYSTEM,
    val selectedCurrency: Currency = Currency.PLN,
    val isWipingData: Boolean = false,
    val showDangerZoneDialog: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.theme.collect { theme ->
                _uiState.update { it.copy(selectedTheme = theme) }
            }
        }
        viewModelScope.launch {
            preferencesRepository.currency.collect { currency ->
                _uiState.update { it.copy(selectedCurrency = currency) }
            }
        }
    }

    fun setTheme(theme: AppTheme) {
        preferencesRepository.setTheme(theme)
    }

    fun setCurrency(currency: Currency) {
        preferencesRepository.setCurrency(currency)
    }

    fun showDangerZone() {
        _uiState.update { it.copy(showDangerZoneDialog = true) }
    }

    fun hideDangerZone() {
        _uiState.update { it.copy(showDangerZoneDialog = false) }
    }

    fun wipeAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isWipingData = true) }
            try {
                transactionDao.deleteAll()
                categoryDao.deleteAll()
            } finally {
                _uiState.update { it.copy(isWipingData = false, showDangerZoneDialog = false) }
            }
        }
    }
}

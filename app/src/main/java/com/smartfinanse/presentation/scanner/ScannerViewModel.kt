package com.smartfinanse.presentation.scanner

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfinanse.data.scanner.AiParsingException
import com.smartfinanse.data.scanner.OcrException
import com.smartfinanse.data.scanner.OcrManager
import com.smartfinanse.data.scanner.ParsedReceipt
import com.smartfinanse.data.scanner.ReceiptParserAi
import com.smartfinanse.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ScannerUiState {
    object Idle : ScannerUiState()
    data class Loading(val message: String) : ScannerUiState()
    data class Success(
        val parsedReceipt: ParsedReceipt, 
        val resolvedCategoryId: Long?
    ) : ScannerUiState()
    data class Error(val message: String) : ScannerUiState()
}

@HiltViewModel
class ScannerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val ocrManager = OcrManager(context)
    private val receiptParserAi = ReceiptParserAi()

    private val _uiState = MutableStateFlow<ScannerUiState>(ScannerUiState.Idle)
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    fun processReceipt(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = ScannerUiState.Loading("Odczytywanie tekstu...")
            try {
                // Krok 1: OCR lokalnie
                val rawText = ocrManager.extractTextFromImage(uri)
                if (rawText.isBlank()) {
                    throw OcrException("Nie rozpoznano żadnego tekstu. Spróbuj zrobić wyraźniejsze zdjęcie.")
                }

                _uiState.value = ScannerUiState.Loading("Sztuczna inteligencja analizuje dane...")

                // Krok 2: Pobierz aktualne kategorie
                val categories = categoryRepository.getCategories(true).first()
                val categoryNames = categories.map { it.name }

                // Krok 3: Gemini API
                val parsedReceipt = receiptParserAi.parseReceiptText(rawText, categoryNames)

                // Krok 4: Dopasowanie kategorii do ID
                val resolvedCategory = categories.find { it.name.equals(parsedReceipt.kategoria, ignoreCase = true) }

                _uiState.value = ScannerUiState.Success(parsedReceipt, resolvedCategory?.id)

            } catch (e: OcrException) {
                _uiState.value = ScannerUiState.Error(e.message ?: "Błąd odczytu tekstu")
            } catch (e: AiParsingException) {
                _uiState.value = ScannerUiState.Error(e.message ?: "Błąd analizy sztucznej inteligencji")
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = ScannerUiState.Error("Wystąpił nieoczekiwany błąd: ${e.message}")
            }
        }
    }

    fun clearState() {
        _uiState.value = ScannerUiState.Idle
    }
}

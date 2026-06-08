package com.smartfinanse.presentation.scanner

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfinanse.data.scanner.ScannerException
import com.smartfinanse.data.scanner.ParsedReceipt
import com.smartfinanse.data.scanner.ReceiptParserAi
import com.smartfinanse.domain.repository.CategoryRepository
import com.smartfinanse.domain.repository.StoreRepository
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
        val resolvedCategoryId: Long?,
        val resolvedStoreId: Long?
    ) : ScannerUiState()
    data class Error(val exception: Exception) : ScannerUiState()
}

@HiltViewModel
class ScannerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val categoryRepository: CategoryRepository,
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val receiptParserAi = ReceiptParserAi()

    private val _uiState = MutableStateFlow<ScannerUiState>(ScannerUiState.Idle)
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    private var lastProcessedUri: Uri? = null

    fun processReceipt(uri: Uri) {
        lastProcessedUri = uri
        viewModelScope.launch {
            if (!isInternetAvailable()) {
                _uiState.value = ScannerUiState.Error(com.smartfinanse.data.scanner.ScannerException.NetworkError)
                return@launch
            }

            _uiState.value = ScannerUiState.Loading("Przygotowywanie obrazu...")
            try {
                // Krok 1: Wczytanie obrazu
                val bitmap = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    val source = android.graphics.ImageDecoder.createSource(context.contentResolver, uri)
                    android.graphics.ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.allocator = android.graphics.ImageDecoder.ALLOCATOR_SOFTWARE
                        decoder.isMutableRequired = true
                    }
                } else {
                    @Suppress("DEPRECATION")
                    android.provider.MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }

                _uiState.value = ScannerUiState.Loading("Sztuczna inteligencja analizuje dane...")

                // Krok 2: Pobierz aktualne kategorie
                val categories = categoryRepository.getCategories(true).first()
                val categoryNames = categories.map { it.name }

                // Krok 3: Gemini API
                val parsedReceipt = receiptParserAi.parseReceiptImage(bitmap, categoryNames)

                // Krok 4: Dopasowanie kategorii i sklepu do ID
                val resolvedCategory = categories.find { it.name.equals(parsedReceipt.kategoria, ignoreCase = true) }
                
                val stores = storeRepository.getAllStores().first()
                val resolvedStore = stores.find { it.name.equals(parsedReceipt.sklep, ignoreCase = true) }

                _uiState.value = ScannerUiState.Success(parsedReceipt, resolvedCategory?.id, resolvedStore?.id)

            } catch (e: com.smartfinanse.data.scanner.ScannerException) {
                com.smartfinanse.utils.FileLogger.logError("ScannerViewModel", "ScannerException: ${e.javaClass.simpleName}", e)
                _uiState.value = ScannerUiState.Error(e)
            } catch (e: Exception) {
                com.smartfinanse.utils.FileLogger.logError("ScannerViewModel", "Unexpected exception: ${e.message}", e)
                e.printStackTrace()
                _uiState.value = ScannerUiState.Error(Exception("Wystąpił nieoczekiwany błąd: ${e.message}"))
            }
        }
    }

    fun retryLastReceipt() {
        lastProcessedUri?.let { processReceipt(it) }
    }

    fun clearState() {
        _uiState.value = ScannerUiState.Idle
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}

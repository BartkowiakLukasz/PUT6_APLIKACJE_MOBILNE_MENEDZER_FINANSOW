package com.smartfinanse.presentation.scanner

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.smartfinanse.presentation.common.SmartFinanseTopAppBar
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import android.app.Activity
import android.content.ContextWrapper
import androidx.activity.result.IntentSenderRequest
import android.content.Context
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import kotlinx.coroutines.launch

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddWithPreFill: (amount: Double, description: String, storeName: String, storeId: Long?, date: String?, categoryId: Long?, isCash: Boolean?, isFallbackCategory: Boolean) -> Unit,
    viewModel: ScannerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val options = remember {
        GmsDocumentScannerOptions.Builder()
            .setGalleryImportAllowed(true) // Pozwala też wczytać z galerii bezpośrednio ze skanera
            .setPageLimit(1)
            .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_JPEG)
            .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
            .build()
    }

    val scanner = remember { GmsDocumentScanning.getClient(options) }
    val coroutineScope = rememberCoroutineScope()

    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultData = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            resultData?.pages?.let { pages ->
                if (pages.isNotEmpty()) {
                    val imageUri = pages[0].imageUri
                    viewModel.processReceipt(imageUri)
                }
            }
        }
    }

    val pickGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.processReceipt(uri)
            }
        }
    )



    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is ScannerUiState.Success -> {
                onNavigateToAddWithPreFill(
                    state.parsedReceipt.kwota,
                    state.parsedReceipt.opis,
                    state.parsedReceipt.sklep,
                    state.resolvedStoreId,
                    state.parsedReceipt.data,
                    state.resolvedCategoryId,
                    state.parsedReceipt.czyGotowka,
                    state.isFallbackCategory
                )
                viewModel.clearState()
            }
            is ScannerUiState.Error -> {
                when (val e = state.exception) {
                    is com.smartfinanse.data.scanner.ScannerException.ServerBusy -> {
                        val result = snackbarHostState.showSnackbar(
                            message = "Serwery AI są zajęte. Spróbuj ponownie za chwilę.",
                            actionLabel = "Ponów"
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            viewModel.retryLastReceipt()
                        } else {
                            viewModel.clearState()
                        }
                    }
                    is com.smartfinanse.data.scanner.ScannerException.InvalidImage -> {
                        snackbarHostState.showSnackbar("Nie udało się odczytać danych. Upewnij się, że zdjęcie jest wyraźne.")
                        viewModel.clearState()
                    }
                    is com.smartfinanse.data.scanner.ScannerException.NetworkError -> {
                        snackbarHostState.showSnackbar("Brak dostępu do internetu. Połączenie z siecią jest wymagane do analizy paragonu przez AI.")
                        viewModel.clearState()
                    }
                    else -> {
                        snackbarHostState.showSnackbar(e.message ?: "Wystąpił nieoczekiwany błąd.")
                        viewModel.clearState()
                    }
                }
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            SmartFinanseTopAppBar(
                title = { Text("Skaner Paragonów") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Wstecz"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is ScannerUiState.Loading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(state.message)
                    }
                }
                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Button(
                            onClick = {
                                val activity = context.findActivity()
                                if (activity != null) {
                                    scanner.getStartScanIntent(activity)
                                        .addOnSuccessListener { intentSender ->
                                            scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                                        }
                                        .addOnFailureListener { e ->
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Nie udało się otworzyć skanera: ${e.message}")
                                            }
                                        }
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Błąd kontekstu aplikacji")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        ) {
                            Text("Skanuj Paragon (ML Kit)")
                        }

                        OutlinedButton(
                            onClick = { pickGalleryLauncher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Wybierz z Galerii")
                        }
                        
                        if (!cameraPermissionState.status.isGranted) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Mimo że skaner prosi o uprawnienia, w razie potrzeby system je obsłuży automatycznie.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

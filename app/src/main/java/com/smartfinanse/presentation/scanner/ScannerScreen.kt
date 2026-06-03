package com.smartfinanse.presentation.scanner

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(
    onOpenDrawer: () -> Unit,
    onNavigateToAddWithPreFill: (amount: Double, description: String, date: String?, categoryId: Long?) -> Unit,
    viewModel: ScannerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    // Przygotowanie pliku docelowego na zdjęcie z aparatu
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    
    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                photoUri?.let { uri -> viewModel.processReceipt(uri) }
            }
        }
    )

    val pickGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.processReceipt(uri)
            }
        }
    )

    fun createPhotoUri(): Uri {
        val file = File(context.cacheDir, "receipt_photo_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is ScannerUiState.Success -> {
                onNavigateToAddWithPreFill(
                    state.parsedReceipt.kwota,
                    state.parsedReceipt.sklep,
                    state.parsedReceipt.data,
                    state.resolvedCategoryId
                )
                viewModel.clearState()
            }
            is ScannerUiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.clearState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Skaner Paragonów") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
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
                                if (cameraPermissionState.status.isGranted) {
                                    photoUri = createPhotoUri()
                                    takePhotoLauncher.launch(photoUri!!)
                                } else {
                                    cameraPermissionState.launchPermissionRequest()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        ) {
                            Text("Zrób Zdjęcie Aparatem")
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
                                "Aplikacja potrzebuje dostępu do aparatu, by skanować paragony. " +
                                "Możesz też użyć zdjęć z galerii.",
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

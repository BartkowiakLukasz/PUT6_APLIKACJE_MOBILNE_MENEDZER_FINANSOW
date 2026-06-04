package com.smartfinanse.presentation.export

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartfinanse.presentation.common.SmartFinanseTopAppBar
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    onNavigateBack: () -> Unit,
    viewModel: ExportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val context = LocalContext.current

    val shareIntentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {}
    )

    fun shareFile(uri: Uri, mimeType: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(shareIntent, "Udostępnij dane")
        shareIntentLauncher.launch(chooser)
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val type = context.contentResolver.getType(uri)
            val extension = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && nameIndex != -1) {
                    cursor.getString(nameIndex).substringAfterLast('.', "")
                } else ""
            } ?: ""
            
            if (extension.equals("json", ignoreCase = true) || type?.contains("json") == true) {
                viewModel.prepareRestoreFromJson(uri)
            } else {
                viewModel.mergeFromCsv(uri)
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.exportSuccess, uiState.importSuccess, uiState.error) {
        uiState.exportSuccess?.let { success ->
            val result = snackbarHostState.showSnackbar(
                message = "${success.message} (W formacie ${success.format})",
                actionLabel = "Udostępnij",
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                val mimeType = if (success.format == "CSV") "text/csv" else "application/json"
                shareFile(success.uri, mimeType)
            }
            viewModel.clearMessages()
        }

        uiState.importSuccess?.let { success ->
            val msg = buildString {
                append(success.message)
                append(" (${success.importedCount})")
                if (success.duplicatesSkipped > 0) {
                    append("\nPominięto ${success.duplicatesSkipped} duplikatów.")
                }
            }
            val result = snackbarHostState.showSnackbar(
                message = msg,
                actionLabel = "Sprawdź",
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                onNavigateBack()
            }
            viewModel.clearMessages()
        }

        uiState.error?.let { error ->
            val actionLabel = when(error.action) {
                ErrorAction.RETRY -> "Spróbuj ponownie"
                ErrorAction.HELP -> "Pomoc"
                null -> null
            }
            val result = snackbarHostState.showSnackbar(
                message = error.reason,
                actionLabel = actionLabel,
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                if (error.action == ErrorAction.RETRY) {
                    // Retry logic if applicable, for now just close
                }
            }
            viewModel.clearMessages()
        }
    }

    if (uiState.showOverwriteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.cancelRestoreFromJson() },
            title = { Text("Ostrzeżenie przed utratą danych") },
            text = { Text("Czy na pewno chcesz kontynuować? Obecne dane zostaną nadpisane i mogą zostać bezpowrotnie utracone.") },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmRestoreFromJson() }) {
                    Text("Kontynuuj", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cancelRestoreFromJson() }) {
                    Text("Anuluj")
                }
            }
        )
    }

    var showDateRangePicker by remember { mutableStateOf(false) }

    if (showDateRangePicker) {
        val dateRangePickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = uiState.startDate,
            initialSelectedEndDateMillis = uiState.endDate
        )
        DatePickerDialog(
            onDismissRequest = { showDateRangePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val start = dateRangePickerState.selectedStartDateMillis
                    val end = dateRangePickerState.selectedEndDateMillis
                    if (start != null && end != null) {
                        viewModel.setCustomTimeFilter(start, end)
                    }
                    showDateRangePicker = false
                }) { Text("Wybierz") }
            },
            dismissButton = {
                TextButton(onClick = { showDateRangePicker = false }) { Text("Anuluj") }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.weight(1f),
                title = { Text("Wybierz zakres", modifier = Modifier.padding(16.dp)) },
                showModeToggle = false
            )
        }
    }



    Scaffold(
        topBar = {
            SmartFinanseTopAppBar(
                title = { Text("Eksport i Import") },
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Eksport") }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Import") }
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                if (selectedTabIndex == 0) {
                    ExportContent(
                        uiState = uiState,
                        onTimeFilterSelected = { filter ->
                            if (filter == com.smartfinanse.presentation.dashboard.TimeFilter.CUSTOM) {
                                showDateRangePicker = true
                            } else {
                                viewModel.setTimeFilter(filter)
                            }
                        },
                        onToggleCategory = viewModel::toggleCategorySelection,
                        onToggleCash = viewModel::toggleCashOnly,
                        onToggleCard = viewModel::toggleCardOnly,
                        onExportCsv = {
                            viewModel.generateCsv()
                        },
                        onExportJson = {
                            viewModel.generateJson()
                        }
                    )
                } else {
                    ImportContent(
                        uiState = uiState,
                        onImportClick = { importLauncher.launch("*/*") }
                    )
                }

                if (uiState.isExporting || uiState.isImporting) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExportContent(
    uiState: ExportUiState,
    onTimeFilterSelected: (com.smartfinanse.presentation.dashboard.TimeFilter) -> Unit,
    onToggleCategory: (Long) -> Unit,
    onToggleCash: (Boolean) -> Unit,
    onToggleCard: (Boolean) -> Unit,
    onExportCsv: () -> Unit,
    onExportJson: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Zakres Dat (dla CSV)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        item {
            val labelText = if (uiState.startDate != null && uiState.endDate != null) {
                val sdf = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
                "Od: ${sdf.format(java.util.Date(uiState.startDate))} - Do: ${sdf.format(java.util.Date(uiState.endDate))}"
            } else {
                "Wybierz zakres dat (Opcjonalnie)"
            }

            OutlinedButton(
                onClick = { onTimeFilterSelected(com.smartfinanse.presentation.dashboard.TimeFilter.CUSTOM) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isExporting && !uiState.isImporting
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(labelText)
            }
        }
        item {
            Divider()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Filtruj Kategorie (dla CSV)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        
        item {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.categories.forEach { cat ->
                    FilterChip(
                        selected = uiState.selectedCategories.contains(cat.id),
                        onClick = { onToggleCategory(cat.id) },
                        label = { Text(cat.name) },
                        enabled = !uiState.isExporting && !uiState.isImporting,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                }
            }
        }
        
        item {
            Divider()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Typ Płatności", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = uiState.exportCashOnly,
                    onCheckedChange = onToggleCash,
                    enabled = !uiState.isExporting && !uiState.isImporting
                )
                Text("Tylko Gotówka")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = uiState.exportCardOnly,
                    onCheckedChange = onToggleCard,
                    enabled = !uiState.isExporting && !uiState.isImporting
                )
                Text("Tylko Karta")
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onExportCsv,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isExporting && !uiState.isImporting
            ) {
                Text("Wygeneruj raport CSV")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onExportJson,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isExporting && !uiState.isImporting
            ) {
                Text("Pełna Kopia Zapasowa (JSON)")
            }
        }
    }
}

@Composable
fun ImportContent(
    uiState: ExportUiState,
    onImportClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Importowanie Danych", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
            "Wybierz plik, z którego chcesz zaimportować dane.\n- Wybór pliku CSV złączy dane (Merge) omijając duplikaty.\n- Wybór pliku JSON wyczyści aktualną bazę i wgra jej stan 1:1 z kopii (Wipe & Restore).",
            style = MaterialTheme.typography.bodyMedium
        )
        
        Button(
            onClick = onImportClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isExporting && !uiState.isImporting
        ) {
            Text("Wybierz Plik (CSV / JSON)")
        }
        

    }
}

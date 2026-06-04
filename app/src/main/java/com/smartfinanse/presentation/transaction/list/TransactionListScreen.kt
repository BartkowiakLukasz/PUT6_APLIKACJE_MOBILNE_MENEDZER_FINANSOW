package com.smartfinanse.presentation.transaction.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartfinanse.R
import com.smartfinanse.domain.model.Category
import com.smartfinanse.presentation.dashboard.TimeFilter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onOpenDrawer: () -> Unit,
    viewModel: TransactionListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDateRangePicker by remember { mutableStateOf(false) }
    var showCategorySheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    if (showDateRangePicker) {
        val dateRangePickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = uiState.customStartDate,
            initialSelectedEndDateMillis = uiState.customEndDate
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

    if (showCategorySheet) {
        val focusManager = LocalFocusManager.current
        ModalBottomSheet(
            onDismissRequest = { showCategorySheet = false },
            sheetState = bottomSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { focusManager.clearFocus() })
                    }
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.categorySearchQuery,
                    onValueChange = viewModel::onCategorySearchQueryChanged,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Szukaj kategorii...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Szukaj")
                    },
                    trailingIcon = {
                        if (uiState.categorySearchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onCategorySearchQueryChanged("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Wyczyść")
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        ListItem(
                            headlineContent = { Text("Wszystkie Kategorie", fontWeight = FontWeight.Bold) },
                            modifier = Modifier.clickable {
                                viewModel.onCategorySelected(null)
                                showCategorySheet = false
                            }
                        )
                    }
                    items(uiState.categories, key = { it.id }) { category ->
                        ListItem(
                            headlineContent = { Text(category.name) },
                            modifier = Modifier.clickable {
                                viewModel.onCategorySelected(category.id)
                                showCategorySheet = false
                            }
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.transaction_list_title)) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToDashboard) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Pulpit"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Dodaj wydatek"
                )
            }
        }
    ) { innerPadding ->
        TransactionListContent(
            uiState = uiState,
            onOpenCategorySheet = {
                viewModel.onCategorySearchQueryChanged("") // Reset search before open
                showCategorySheet = true
            },
            onTimeFilterSelected = { filter ->
                if (filter == TimeFilter.CUSTOM) {
                    showDateRangePicker = true
                } else {
                    viewModel.setTimeFilter(filter)
                }
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun TransactionListContent(
    uiState: TransactionListUiState,
    onOpenCategorySheet: () -> Unit,
    onTimeFilterSelected: (TimeFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        
        // Filtry czasu
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(TimeFilter.entries.toTypedArray()) { filter ->
                val labelText = if (filter == TimeFilter.CUSTOM && uiState.customStartDate != null && uiState.customEndDate != null) {
                    formatRange(uiState.customStartDate, uiState.customEndDate)
                } else {
                    filter.label
                }
                
                FilterChip(
                    selected = uiState.selectedTimeFilter == filter,
                    onClick = { onTimeFilterSelected(filter) },
                    label = { Text(labelText) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }

        // Kategoria - Przycisk otwierający ModalBottomSheet
        val selectedCategoryName = uiState.categories.find { it.id == uiState.selectedCategoryId }?.name
        val categoryLabel = if (uiState.selectedCategoryId == null) {
            "Wszystkie Kategorie"
        } else {
            "Kategoria: ${selectedCategoryName ?: "Nieznana"}"
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onOpenCategorySheet,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.FilterList, contentDescription = "Filtruj")
                Spacer(modifier = Modifier.width(8.dp))
                Text(categoryLabel)
            }
        }

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.transaction_list_error),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            uiState.groupedTransactions.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.transaction_list_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp) // Miejsce na FAB
                ) {
                    uiState.groupedTransactions.forEach { (date, transactions) ->
                        stickyHeader {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = date,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        items(
                            items = transactions,
                            key = { it.id }
                        ) { transaction ->
                            TransactionListItem(transaction = transaction)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionListItem(transaction: TransactionItemUi) {
    val categoryLabel = transaction.categoryName
        ?: stringResource(R.string.no_category)
    val paymentLabel = stringResource(
        if (transaction.isCash) R.string.payment_cash else R.string.payment_card
    )

    ListItem(
        headlineContent = { Text(transaction.title) },
        supportingContent = {
            Text("$categoryLabel · $paymentLabel")
        },
        trailingContent = {
            Text(
                text = transaction.amountFormatted,
                style = MaterialTheme.typography.titleMedium
            )
        }
    )
}

private fun formatRange(start: Long?, end: Long?): String {
    if (start == null || end == null) return "Zakres"
    val sdf = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
    return "${sdf.format(Date(start))} - ${sdf.format(Date(end))}"
}

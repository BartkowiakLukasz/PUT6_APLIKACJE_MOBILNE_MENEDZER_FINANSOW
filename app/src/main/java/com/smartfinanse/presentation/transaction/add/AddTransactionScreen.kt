package com.smartfinanse.presentation.transaction.add

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.smartfinanse.presentation.common.SmartFinanseTopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ListItem
import com.smartfinanse.presentation.common.StoreIconRenderer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.smartfinanse.domain.util.capitalizeFirst
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import com.smartfinanse.R
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartfinanse.domain.model.Category
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.smartfinanse.domain.util.capitalizeFirst

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToScanner: () -> Unit,
    onNavigateToCategoryAdd: (Boolean) -> Unit,
    onNavigateToStoreAdd: () -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            SmartFinanseTopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (uiState.isExpense) R.string.add_expense_title
                            else R.string.add_income_title
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wróć")
                    }
                }
            )
        }
    ) { innerPadding ->
        AddTransactionContent(
            uiState = uiState,
            modifier = Modifier.padding(innerPadding),
            onAmountChange = viewModel::onAmountChange,
            onDescriptionChange = viewModel::onDescriptionChange,
            onDateChange = viewModel::onDateChange,
            onIsCashChange = viewModel::onIsCashChange,
            onIsRecurringChange = viewModel::onIsRecurringChange,
            onCategorySelected = viewModel::onCategorySelected,
            onCategorySearchQueryChanged = viewModel::onCategorySearchQueryChanged,
            onStoreSelected = viewModel::onStoreSelected,
            onStoreSearchQueryChanged = viewModel::onStoreSearchQueryChanged,
            onSaveClick = viewModel::saveTransaction,
            onShowAddCategoryClick = { onNavigateToCategoryAdd(uiState.isExpense) },
            onNavigateToScanner = onNavigateToScanner,
            onNavigateToStoreAdd = onNavigateToStoreAdd,
            showScanner = uiState.isExpense
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTransactionContent(
    uiState: AddTransactionUiState,
    modifier: Modifier = Modifier,
    onAmountChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDateChange: (Long) -> Unit,
    onIsCashChange: (Boolean) -> Unit,
    onIsRecurringChange: (Boolean) -> Unit,
    onCategorySelected: (Long) -> Unit,
    onCategorySearchQueryChanged: (String) -> Unit,
    onStoreSelected: (Long?) -> Unit,
    onStoreSearchQueryChanged: (String) -> Unit,
    onSaveClick: () -> Unit,
    onShowAddCategoryClick: () -> Unit,
    onNavigateToScanner: () -> Unit,
    onNavigateToStoreAdd: () -> Unit,
    showScanner: Boolean
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var storeSearchActive by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (showScanner) {
            Button(
                onClick = onNavigateToScanner,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Zeskanuj paragon (OCR & AI)")
            }
        }

        OutlinedTextField(
            value = uiState.amount,
            onValueChange = onAmountChange,
            label = { Text("Kwota (PLN)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true,
            isError = uiState.amountError != null,
            supportingText = {
                uiState.amountError?.let { errorMsg ->
                    Text(text = errorMsg, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        if (uiState.isExpense) {
            val filteredStores = remember(uiState.recentStores, uiState.storeSearchQuery) {
                if (uiState.storeSearchQuery.isEmpty()) {
                    uiState.recentStores.take(4)
                } else {
                    uiState.recentStores.filter { 
                        it.name.contains(uiState.storeSearchQuery, ignoreCase = true) 
                    }
                }
            }

            DockedSearchBar(
                query = uiState.storeSearchQuery,
                onQueryChange = onStoreSearchQueryChanged,
                onSearch = { storeSearchActive = false },
                active = storeSearchActive,
                onActiveChange = { storeSearchActive = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Sklep (np. Lidl)") },
                leadingIcon = { Icon(Icons.Default.Store, contentDescription = null) },
                trailingIcon = {
                    if (storeSearchActive && uiState.storeSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { onStoreSearchQueryChanged("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Wyczyść")
                        }
                    } else if (uiState.selectedStoreId != null) {
                        IconButton(onClick = { onStoreSelected(null) }) {
                            Icon(Icons.Default.Close, contentDescription = "Usuń sklep")
                        }
                    }
                }
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier.height(200.dp)
                ) {
                    items(filteredStores, key = { it.id }) { store ->
                        ListItem(
                            headlineContent = { Text(store.name.capitalizeFirst()) },
                            leadingContent = {
                                StoreIconRenderer(
                                    iconName = store.iconName,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            modifier = Modifier.clickable {
                                onStoreSelected(store.id)
                                onStoreSearchQueryChanged(store.name.capitalizeFirst())
                                storeSearchActive = false
                            }
                        )
                    }
                    item {
                        ListItem(
                            headlineContent = { Text("Dodaj nowy sklep", color = MaterialTheme.colorScheme.primary) },
                            leadingContent = { Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                            modifier = Modifier.clickable {
                                onNavigateToStoreAdd()
                                storeSearchActive = false
                            }
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = uiState.description,
            onValueChange = onDescriptionChange,
            label = { Text("Opis / Notatka") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Date selector
        OutlinedTextField(
            value = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(uiState.date)),
            onValueChange = { },
            label = { Text("Data") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Wybierz datę")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true }
        )

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.date)
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { onDateChange(it) }
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Anuluj") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // Toggles
        if (uiState.isExpense) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Płatność gotówką", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = uiState.isCash,
                    onCheckedChange = onIsCashChange
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Opłata cykliczna (np. subskrypcja)", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = uiState.isRecurring,
                    onCheckedChange = onIsRecurringChange
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Kategoria", style = MaterialTheme.typography.titleMedium)
                if (uiState.categoryError != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.categoryError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        if (uiState.categories.size > 6) {
            OutlinedTextField(
                value = uiState.categorySearchQuery,
                onValueChange = onCategorySearchQueryChanged,
                placeholder = { Text("Wyszukaj kategorię...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Szukaj")
                },
                trailingIcon = {
                    if (uiState.categorySearchQuery.isNotEmpty()) {
                        IconButton(onClick = { onCategorySearchQueryChanged("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Wyczyść")
                        }
                    }
                }
            )
        }

        val filteredCategories = remember(uiState.categories, uiState.categorySearchQuery) {
            uiState.categories
                .filter { it.name.contains(uiState.categorySearchQuery, ignoreCase = true) }
                .sortedBy { it.name.lowercase() }
        }

        // Categories Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.height(200.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredCategories, key = { it.id }) { category ->
                CategoryItem(
                    category = category,
                    isSelected = category.id == uiState.selectedCategoryId,
                    onClick = { onCategorySelected(category.id) }
                )
            }
            item {
                AddCategoryItem(onClick = onShowAddCategoryClick)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        uiState.globalError?.let { errorMsg ->
            Text(
                text = errorMsg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !uiState.isSaving,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                stringResource(
                    if (uiState.isExpense) R.string.save_expense else R.string.save_income
                )
            )
        }
    }
}

@Composable
private fun CategoryItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            com.smartfinanse.presentation.common.CategoryIconRenderer(
                iconName = category.iconName,
                colorHex = category.colorHex,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = category.name.capitalizeFirst(),
                style = MaterialTheme.typography.bodySmall,
                color = contentColor,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun AddCategoryItem(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Add, contentDescription = "Dodaj", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Inne / Dodaj",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}



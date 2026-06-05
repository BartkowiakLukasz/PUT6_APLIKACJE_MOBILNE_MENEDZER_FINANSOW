package com.smartfinanse.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartfinanse.R
import com.smartfinanse.presentation.transaction.add.AddTransactionTypeSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToAddExpense: () -> Unit,
    onNavigateToAddIncome: () -> Unit,
    onNavigateToCharts: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colorScheme = MaterialTheme.colorScheme
    var showPeriodSheet by remember { mutableStateOf(false) }
    var showDateRangePicker by remember { mutableStateOf(false) }
    var showAddTypeSheet by remember { mutableStateOf(false) }

    val showExpenses = uiState.contentFilter != DashboardContentFilter.INCOME_ONLY
    val showIncome = uiState.contentFilter != DashboardContentFilter.EXPENSES_ONLY
    val periodSuffix = periodLabelForSection(
        uiState.selectedFilter,
        uiState.customStartDate,
        uiState.customEndDate
    )

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

    Scaffold(
        containerColor = colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTypeSheet = true },
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj transakcję")
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colorScheme.primary)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            DashboardHeroRevolut(
                filter = uiState.selectedFilter,
                customStart = uiState.customStartDate,
                customEnd = uiState.customEndDate,
                netBalance = uiState.netBalance,
                totalIncome = uiState.totalIncome,
                totalExpenses = uiState.totalExpenses,
                cashPercent = uiState.cashExpensePercent,
                cardPercent = uiState.cardExpensePercent,
                onPeriodClick = { showPeriodSheet = true },
                onOpenCharts = onNavigateToCharts
            )

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 20.dp,
                        bottom = 88.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        DashboardContentFilterRow(
                            selected = uiState.contentFilter,
                            onSelected = viewModel::setContentFilter
                        )
                    }

                    if (showExpenses && uiState.expenseBreakdown.isNotEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.dashboard_expenses_for_period, periodSuffix),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.onSurface
                            )
                        }
                        items(uiState.expenseBreakdown, key = { "exp-${it.categoryId}" }) { item ->
                            CategoryBreakdownRow(item = item)
                        }
                    }

                    if (showIncome && uiState.incomeBreakdown.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.dashboard_income_for_period, periodSuffix),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.onSurface
                            )
                        }
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(uiState.incomeBreakdown, key = { "inc-${it.categoryId}" }) { item ->
                                    IncomeCategoryCard(item = item)
                                }
                            }
                        }
                    }

                    if (
                        (showExpenses && uiState.expenseBreakdown.isEmpty()) &&
                        (showIncome && uiState.incomeBreakdown.isEmpty())
                    ) {
                        item {
                            Text(
                                text = stringResource(R.string.dashboard_no_transactions_in_period),
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 24.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showPeriodSheet) {
        DashboardPeriodSheet(
            selectedFilter = uiState.selectedFilter,
            onDismiss = { showPeriodSheet = false },
            onFilterSelected = viewModel::setTimeFilter,
            onCustomRangeRequested = { showDateRangePicker = true }
        )
    }

    if (showAddTypeSheet) {
        AddTransactionTypeSheet(
            onDismiss = { showAddTypeSheet = false },
            onAddExpense = {
                showAddTypeSheet = false
                onNavigateToAddExpense()
            },
            onAddIncome = {
                showAddTypeSheet = false
                onNavigateToAddIncome()
            }
        )
    }
}

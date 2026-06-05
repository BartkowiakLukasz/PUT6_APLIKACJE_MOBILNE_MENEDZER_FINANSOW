package com.smartfinanse.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartfinanse.R
import com.smartfinanse.presentation.common.SmartFinanseTopAppBar
import com.smartfinanse.presentation.theme.SmartFinanseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardChartsScreen(
    onNavigateBack: () -> Unit,
    viewModel: DashboardViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val extraColors = SmartFinanseTheme.extraColors
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            SmartFinanseTopAppBar(
                title = { Text(stringResource(R.string.dashboard_charts_title)) },
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
        containerColor = colorScheme.background
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = stringResource(
                        R.string.dashboard_charts_period_hint,
                        periodDisplayLabel(
                            uiState.selectedFilter,
                            uiState.customStartDate,
                            uiState.customEndDate
                        )
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }

            item {
                DashboardContentFilterRow(
                    selected = uiState.contentFilter,
                    onSelected = viewModel::setContentFilter
                )
            }

            item {
                DashboardChartsCard(
                    contentFilter = uiState.contentFilter,
                    incomeBreakdown = uiState.incomeBreakdown,
                    expenseBreakdown = uiState.expenseBreakdown,
                    totalIncome = uiState.totalIncome,
                    totalExpenses = uiState.totalExpenses,
                    netBalance = uiState.netBalance,
                    chartCardBackground = extraColors.chartCardBackground
                )
            }

            val periodSuffix = periodLabelForSection(
                uiState.selectedFilter,
                uiState.customStartDate,
                uiState.customEndDate
            )
            val showExpenses = uiState.contentFilter != DashboardContentFilter.INCOME_ONLY
            val showIncome = uiState.contentFilter != DashboardContentFilter.EXPENSES_ONLY

            if (showExpenses && uiState.expenseBreakdown.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.dashboard_expenses_for_period, periodSuffix),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                items(uiState.expenseBreakdown, key = { "chart-exp-${it.categoryId}" }) { item ->
                    CategoryBreakdownRow(item = item)
                }
            }

            if (showIncome && uiState.incomeBreakdown.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.dashboard_income_for_period, periodSuffix),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(uiState.incomeBreakdown, key = { "chart-inc-${it.categoryId}" }) { item ->
                            IncomeCategoryCard(item = item)
                        }
                    }
                }
            }
        }
    }
}

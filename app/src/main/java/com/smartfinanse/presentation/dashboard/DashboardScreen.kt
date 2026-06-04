package com.smartfinanse.presentation.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartfinanse.presentation.common.MoneyFormatter
import com.smartfinanse.presentation.theme.SmartFinanseTheme
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToAdd: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val extraColors = SmartFinanseTheme.extraColors
    val colorScheme = MaterialTheme.colorScheme
    var showDateRangePicker by remember { mutableStateOf(false) }

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
                onClick = onNavigateToAdd,
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 88.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                DashboardHeroSection(
                    filter = uiState.selectedFilter,
                    customStart = uiState.customStartDate,
                    customEnd = uiState.customEndDate,
                    netBalance = uiState.netBalance,
                    totalIncome = uiState.totalIncome,
                    totalExpenses = uiState.totalExpenses,
                    cashPercent = uiState.cashExpensePercent,
                    cardPercent = uiState.cardExpensePercent
                )
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(TimeFilter.entries.toTypedArray()) { filter ->
                        val labelText = if (
                            filter == TimeFilter.CUSTOM &&
                            uiState.customStartDate != null &&
                            uiState.customEndDate != null
                        ) {
                            formatRange(uiState.customStartDate, uiState.customEndDate)
                        } else {
                            filter.label
                        }

                        FilterChip(
                            selected = uiState.selectedFilter == filter,
                            onClick = {
                                if (filter == TimeFilter.CUSTOM) {
                                    showDateRangePicker = true
                                } else {
                                    viewModel.setTimeFilter(filter)
                                }
                            },
                            label = { Text(labelText) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colorScheme.primaryContainer,
                                selectedLabelColor = colorScheme.onPrimaryContainer,
                                containerColor = colorScheme.surfaceVariant,
                                labelColor = colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = extraColors.chartCardBackground
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (uiState.categoryBreakdown.isNotEmpty()) {
                            DonutChart(
                                items = uiState.categoryBreakdown,
                                totalAmount = uiState.totalAmount,
                                modifier = Modifier
                                    .size(200.dp)
                                    .padding(12.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(200.dp)
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Brak wydatków w wybranym okresie",
                                    color = colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            if (uiState.categoryBreakdown.isNotEmpty()) {
                item {
                    Text(
                        text = "Wydatki według kategorii",
                        style = MaterialTheme.typography.labelLarge,
                        color = colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            items(uiState.categoryBreakdown, key = { it.categoryId }) { item ->
                CategoryBreakdownRow(item = item)
            }
        }
    }
}

@Composable
private fun DashboardHeroSection(
    filter: TimeFilter,
    customStart: Long?,
    customEnd: Long?,
    netBalance: Long,
    totalIncome: Long,
    totalExpenses: Long,
    cashPercent: Int,
    cardPercent: Int
) {
    val colorScheme = MaterialTheme.colorScheme
    val periodLabel = when (filter) {
        TimeFilter.CUSTOM -> formatRange(customStart, customEnd)
        else -> filter.label
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Saldo · $periodLabel",
            style = MaterialTheme.typography.labelLarge,
            color = colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = formatBalance(netBalance),
            style = MaterialTheme.typography.displaySmall,
            color = colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Przychody ${formatPlainAmount(totalIncome)} · Wydatki ${formatPlainAmount(totalExpenses)}",
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant
        )

        if (totalExpenses > 0) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PaymentMethodPill(label = "Gotówka", percent = cashPercent)
                PaymentMethodPill(label = "Karta", percent = cardPercent)
            }
        }
    }
}

@Composable
private fun PaymentMethodPill(label: String, percent: Int) {
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = colorScheme.surfaceVariant
    ) {
        Text(
            text = "$label $percent%",
            style = MaterialTheme.typography.labelLarge,
            color = colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun DonutChart(
    items: List<CategoryBreakdownItem>,
    totalAmount: Long,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val categoryFallback = colorScheme.onSurfaceVariant

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            var startAngle = -90f
            val strokeWidth = 44f

            for (item in items) {
                val sweepAngle = item.percentage * 360f
                val color = parseHexColor(item.colorHex, categoryFallback)

                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )
                startAngle += sweepAngle
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Wydatki",
                style = MaterialTheme.typography.labelLarge,
                color = colorScheme.onSurfaceVariant
            )
            Text(
                text = formatPlainAmount(totalAmount),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
        }
    }
}

@Composable
fun CategoryBreakdownRow(item: CategoryBreakdownItem) {
    val colorScheme = MaterialTheme.colorScheme
    val categoryColor = parseHexColor(item.colorHex, colorScheme.onSurfaceVariant)
    val percentText = (item.percentage * 100).toInt()

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(categoryColor)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onSurface
                    )
                    Text(
                        text = "$percentText%",
                        style = MaterialTheme.typography.labelLarge,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = formatPlainAmount(item.totalAmount),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface
            )
        }
    }
}

private fun parseHexColor(hex: String, fallback: Color): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        fallback
    }
}

private fun formatBalance(amountGrosze: Long): String {
    val zloty = amountGrosze / 100.0
    val formatted = NumberFormat.getNumberInstance(Locale("pl", "PL")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }.format(kotlin.math.abs(zloty))
    val sign = when {
        amountGrosze > 0 -> "+"
        amountGrosze < 0 -> "−"
        else -> ""
    }
    return "$sign$formatted ${MoneyFormatter.currentCurrencySymbol}"
}

private fun formatPlainAmount(amountGrosze: Long): String {
    val zloty = amountGrosze / 100.0
    val formatted = NumberFormat.getNumberInstance(Locale("pl", "PL")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }.format(zloty)
    return "$formatted ${MoneyFormatter.currentCurrencySymbol}"
}

private fun formatRange(start: Long?, end: Long?): String {
    if (start == null || end == null) return "Zakres"
    val sdf = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
    return "${sdf.format(Date(start))} - ${sdf.format(Date(end))}"
}

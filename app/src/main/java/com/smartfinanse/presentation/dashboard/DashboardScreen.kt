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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.smartfinanse.presentation.theme.SmartFinanseTheme
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToHistory: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onOpenDrawer: () -> Unit,
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
        topBar = {
            TopAppBar(
                title = { Text("Smart Finanse") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.List, contentDescription = "Historia transakcji")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = extraColors.mintHeader,
                    titleContentColor = extraColors.onMintHeader,
                    navigationIconContentColor = extraColors.onMintHeader,
                    actionIconContentColor = extraColors.onMintHeader
                )
            )
        },
        containerColor = extraColors.dashboardBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
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
                                        selectedContainerColor = extraColors.filterChipSelected,
                                        selectedLabelColor = extraColors.onMintHeader,
                                        containerColor = colorScheme.surfaceVariant,
                                        labelColor = colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }

                        if (uiState.categoryBreakdown.isNotEmpty()) {
                            DonutChart(
                                items = uiState.categoryBreakdown,
                                totalAmount = uiState.totalAmount,
                                modifier = Modifier
                                    .size(220.dp)
                                    .padding(16.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(220.dp)
                                    .padding(16.dp),
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

                FloatingActionButton(
                    onClick = onNavigateToAdd,
                    containerColor = extraColors.fabContainer,
                    contentColor = extraColors.fabContent,
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 28.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Dodaj wydatek")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(uiState.categoryBreakdown, key = { it.categoryId }) { item ->
                    CategoryBreakdownRow(item = item)
                }
            }
        }
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
            val strokeWidth = 50f

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
                text = "Suma wydatków",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
            Text(
                text = formatAmount(totalAmount),
                style = MaterialTheme.typography.titleLarge,
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

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(categoryColor)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurface
                )
            }
            Text(
                text = formatAmount(item.totalAmount),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
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

private fun formatAmount(amountInGroschen: Long): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("pl", "PL"))
    return formatter.format(amountInGroschen / 100.0)
}

private fun formatRange(start: Long?, end: Long?): String {
    if (start == null || end == null) return "Zakres"
    val sdf = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
    return "${sdf.format(Date(start))} - ${sdf.format(Date(end))}"
}

package com.smartfinanse.presentation.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.smartfinanse.R

fun buildCombinedChartItems(
    incomeBreakdown: List<CategoryBreakdownItem>,
    expenseBreakdown: List<CategoryBreakdownItem>
): List<CategoryBreakdownItem> {
    val total = incomeBreakdown.sumOf { it.totalAmount } + expenseBreakdown.sumOf { it.totalAmount }
    if (total == 0L) return emptyList()
    return (incomeBreakdown + expenseBreakdown)
        .map { item ->
            item.copy(percentage = item.totalAmount.toFloat() / total.toFloat())
        }
        .sortedByDescending { it.totalAmount }
}

@Composable
fun DashboardChartsCard(
    contentFilter: DashboardContentFilter,
    incomeBreakdown: List<CategoryBreakdownItem>,
    expenseBreakdown: List<CategoryBreakdownItem>,
    totalIncome: Long,
    totalExpenses: Long,
    netBalance: Long,
    chartCardBackground: Color,
    modifier: Modifier = Modifier
) {
    val chartSize = 200.dp
    val strokeWidth = 40f

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = chartCardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (contentFilter) {
                DashboardContentFilter.BOTH -> {
                    val combinedItems = buildCombinedChartItems(incomeBreakdown, expenseBreakdown)
                    if (combinedItems.isNotEmpty()) {
                        DonutChart(
                            items = combinedItems,
                            centerTitle = stringResource(R.string.dashboard_chart_combined_label),
                            centerAmountText = formatBalance(netBalance),
                            modifier = Modifier.size(chartSize),
                            strokeWidth = strokeWidth
                        )
                    } else {
                        ChartEmptyState(
                            text = stringResource(R.string.dashboard_no_transactions_in_period),
                            size = chartSize
                        )
                    }
                }
                DashboardContentFilter.EXPENSES_ONLY -> {
                    SingleTypeChart(
                        items = expenseBreakdown,
                        centerTitle = stringResource(R.string.dashboard_chart_expense_label),
                        totalAmount = totalExpenses,
                        isIncome = false,
                        emptyText = stringResource(R.string.dashboard_no_expense_in_period),
                        chartSize = chartSize,
                        strokeWidth = strokeWidth
                    )
                }
                DashboardContentFilter.INCOME_ONLY -> {
                    SingleTypeChart(
                        items = incomeBreakdown,
                        centerTitle = stringResource(R.string.dashboard_chart_income_label),
                        totalAmount = totalIncome,
                        isIncome = true,
                        emptyText = stringResource(R.string.dashboard_no_income_in_period),
                        chartSize = chartSize,
                        strokeWidth = strokeWidth
                    )
                }
            }
        }
    }
}

@Composable
private fun SingleTypeChart(
    items: List<CategoryBreakdownItem>,
    centerTitle: String,
    totalAmount: Long,
    isIncome: Boolean,
    emptyText: String,
    chartSize: androidx.compose.ui.unit.Dp,
    strokeWidth: Float
) {
    if (items.isNotEmpty()) {
        DonutChart(
            items = items,
            centerTitle = centerTitle,
            centerAmountText = formatChartCenterAmount(totalAmount, isIncome),
            modifier = Modifier.size(chartSize),
            strokeWidth = strokeWidth
        )
    } else {
        ChartEmptyState(text = emptyText, size = chartSize)
    }
}

@Composable
private fun ChartEmptyState(
    text: String,
    size: androidx.compose.ui.unit.Dp
) {
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .size(size)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DonutChart(
    items: List<CategoryBreakdownItem>,
    centerTitle: String,
    centerAmountText: String,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 40f
) {
    val colorScheme = MaterialTheme.colorScheme
    val categoryFallback = colorScheme.onSurfaceVariant

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.matchParentSize()) {
            var startAngle = -90f
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
                text = centerAmountText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface,
                maxLines = 1
            )
            Text(
                text = centerTitle,
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.onSurfaceVariant
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

@Composable
fun IncomeCategoryCard(item: CategoryBreakdownItem) {
    val colorScheme = MaterialTheme.colorScheme
    val categoryColor = parseHexColor(item.colorHex, colorScheme.primary)
    val percentText = (item.percentage * 100).toInt()

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        modifier = Modifier.width(168.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(categoryColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${formatPlainAmount(item.totalAmount)} ($percentText%)",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

fun parseHexColor(hex: String, fallback: Color): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        fallback
    }
}

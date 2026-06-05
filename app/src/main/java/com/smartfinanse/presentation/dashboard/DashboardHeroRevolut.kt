package com.smartfinanse.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartfinanse.R
import com.smartfinanse.presentation.theme.SmartFinanseTheme

@Composable
fun DashboardHeroRevolut(
    filter: TimeFilter,
    customStart: Long?,
    customEnd: Long?,
    netBalance: Long,
    totalIncome: Long,
    totalExpenses: Long,
    cashPercent: Int,
    cardPercent: Int,
    onPeriodClick: () -> Unit,
    onOpenCharts: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extraColors = SmartFinanseTheme.extraColors
    val periodLabel = periodDisplayLabel(filter, customStart, customEnd)
    val onHero = extraColors.dashboardHeroOnGradient

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.46f)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        extraColors.dashboardHeroGradientStart,
                        extraColors.dashboardHeroGradientEnd
                    )
                )
            )
    ) {
        IconButton(
            onClick = onOpenCharts,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 4.dp, end = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.BarChart,
                contentDescription = stringResource(R.string.dashboard_open_charts),
                tint = onHero
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .clickable(onClick = onPeriodClick)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.dashboard_balance_period, periodLabel),
                    style = MaterialTheme.typography.labelLarge,
                    color = onHero.copy(alpha = 0.85f)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = onHero.copy(alpha = 0.85f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = formatBalance(netBalance),
                style = MaterialTheme.typography.displayLarge,
                color = onHero,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(
                    R.string.dashboard_income_expense_line,
                    formatPlainAmount(totalIncome),
                    formatPlainAmount(totalExpenses)
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = onHero.copy(alpha = 0.8f)
            )

            if (totalExpenses > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HeroPaymentPill(label = "Gotówka", percent = cashPercent, onHero = onHero)
                    HeroPaymentPill(label = "Karta", percent = cardPercent, onHero = onHero)
                }
            }
        }
    }
}

@Composable
private fun HeroPaymentPill(
    label: String,
    percent: Int,
    onHero: androidx.compose.ui.graphics.Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = onHero.copy(alpha = 0.18f)
    ) {
        Text(
            text = "$label $percent%",
            style = MaterialTheme.typography.labelLarge,
            color = onHero,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

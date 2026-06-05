package com.smartfinanse.presentation.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smartfinanse.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardPeriodSheet(
    selectedFilter: TimeFilter,
    onDismiss: () -> Unit,
    onFilterSelected: (TimeFilter) -> Unit,
    onCustomRangeRequested: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val presetFilters = TimeFilter.entries.filter { it != TimeFilter.CUSTOM }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = stringResource(R.string.dashboard_period_sheet_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            presetFilters.forEach { filter ->
                val selected = selectedFilter == filter
                ListItem(
                    headlineContent = { Text(filter.label) },
                    trailingContent = {
                        if (selected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onFilterSelected(filter)
                            onDismiss()
                        }
                )
            }

            ListItem(
                headlineContent = {
                    Text(stringResource(R.string.dashboard_select_custom_range))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDismiss()
                        onCustomRangeRequested()
                    }
            )
        }
    }
}

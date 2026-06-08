package com.smartfinanse.presentation.transaction.add

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
fun AddTransactionTypeSheet(
    onDismiss: () -> Unit,
    onAddExpense: () -> Unit,
    onAddIncome: () -> Unit,
    onAddSubscription: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                text = stringResource(R.string.add_transaction_sheet_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
            ListItem(
                headlineContent = {
                    Text(stringResource(R.string.add_expense))
                },
                supportingContent = {
                    Text(stringResource(R.string.add_expense_subtitle))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onAddExpense()
                        onDismiss()
                    }
            )
            ListItem(
                headlineContent = {
                    Text(stringResource(R.string.add_income))
                },
                supportingContent = {
                    Text(stringResource(R.string.add_income_subtitle))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onAddIncome()
                        onDismiss()
                    }
            )
            ListItem(
                headlineContent = {
                    Text("Dodaj subskrypcję")
                },
                supportingContent = {
                    Text("Cykliczna opłata")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onAddSubscription()
                        onDismiss()
                    }
            )
        }
    }
}

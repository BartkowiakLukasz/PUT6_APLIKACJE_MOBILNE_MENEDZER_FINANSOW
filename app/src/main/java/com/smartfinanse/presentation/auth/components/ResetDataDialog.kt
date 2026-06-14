package com.smartfinanse.presentation.auth.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ResetDataDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Twardy reset aplikacji",
                color = MaterialTheme.colorScheme.error
            )
        },
        text = {
            Text(
                text = "Czy na pewno chcesz zresetować PIN? Ta operacja jest nieodwracalna i spowoduje całkowite usunięcie Twojej historii wydatków, kategorii, sklepów oraz wszystkich ustawień."
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Wyczyść wszystko",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Anuluj")
            }
        }
    )
}

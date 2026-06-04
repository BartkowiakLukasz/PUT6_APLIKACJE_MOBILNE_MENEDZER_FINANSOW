package com.smartfinanse.presentation.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartfinanse.domain.repository.AppTheme
import com.smartfinanse.presentation.common.SmartFinanseTopAppBar
import com.smartfinanse.domain.repository.Currency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if (uiState.showDangerZoneDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDangerZone() },
            title = { Text("Uwaga - Strefa Zagrożenia", modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
            text = { Text("Czy na pewno chcesz bezpowrotnie skasować wszystkie transakcje, kategorie i konta? Tej operacji nie da się cofnąć!", textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
            confirmButton = {
                Button(
                    onClick = { viewModel.wipeAllData() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Tak, skasuj wszystko")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDangerZone() }) {
                    Text("Anuluj")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            SmartFinanseTopAppBar(
                title = { Text("Ustawienia") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Wstecz"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text("Personalizacja", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Wygląd Aplikacji", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Motyw")
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(onClick = { expanded = true }) {
                            Text(when (uiState.selectedTheme) {
                                AppTheme.SYSTEM -> "Systemowy"
                                AppTheme.LIGHT -> "Jasny"
                                AppTheme.DARK -> "Ciemny"
                            })
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(text = { Text("Systemowy") }, onClick = { viewModel.setTheme(AppTheme.SYSTEM); expanded = false })
                            DropdownMenuItem(text = { Text("Jasny") }, onClick = { viewModel.setTheme(AppTheme.LIGHT); expanded = false })
                            DropdownMenuItem(text = { Text("Ciemny") }, onClick = { viewModel.setTheme(AppTheme.DARK); expanded = false })
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Domyślna Waluta", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Waluta")
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(onClick = { expanded = true }) {
                            Text("${uiState.selectedCurrency.name} (${uiState.selectedCurrency.symbol})")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            Currency.entries.forEach { currency ->
                                DropdownMenuItem(
                                    text = { Text("${currency.name} (${currency.symbol})") },
                                    onClick = { viewModel.setCurrency(currency); expanded = false }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Zarządzanie Danymi", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = "Danger", tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Strefa Zagrożenia", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.showDangerZone() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Usuń wszystkie dane", color = MaterialTheme.colorScheme.onError)
                        }
                    }
                }
            }

            item {
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                Text("O aplikacji", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Wersja: v1.0.0-beta", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Autorzy:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                
                Text(
                    text = "▶ Łukasz Bartkowiak (GitHub)",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .clickable {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/BartkowiakLukasz"))
                                context.startActivity(intent)
                            } catch (e: android.content.ActivityNotFoundException) {
                                android.widget.Toast.makeText(context, "Brak przeglądarki internetowej!", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                        .padding(vertical = 4.dp)
                )
                
                Text(
                    text = "▶ Michał Byczko (GitHub)",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .clickable {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Vortexio320"))
                                context.startActivity(intent)
                            } catch (e: android.content.ActivityNotFoundException) {
                                android.widget.Toast.makeText(context, "Brak przeglądarki internetowej!", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                        .padding(vertical = 4.dp)
                )
            }
        }
    }
}

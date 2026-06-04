package com.smartfinanse.presentation.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import com.smartfinanse.presentation.common.SmartFinanseTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

private data class MoreMenuItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    onNavigateTo: (String) -> Unit
) {
    val menuItems = listOf(
        MoreMenuItem(
            title = "Zarządzanie kategoriami",
            subtitle = "Kolory, ikony i nazwy",
            icon = Icons.Default.Build,
            route = "categories"
        ),
        MoreMenuItem(
            title = "Skaner paragonów",
            subtitle = "Zdjęcie i analiza AI",
            icon = Icons.Default.Search,
            route = "scanner"
        ),
        MoreMenuItem(
            title = "Eksport danych",
            subtitle = "Kopia zapasowa i import",
            icon = Icons.Default.Share,
            route = "export"
        ),
        MoreMenuItem(
            title = "Ustawienia",
            subtitle = "Motyw, waluta i dane",
            icon = Icons.Default.Settings,
            route = "settings"
        )
    )

    Scaffold(
        topBar = {
            SmartFinanseTopAppBar(
                title = { Text("Więcej") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(menuItems, key = { it.route }) { item ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    supportingContent = {
                        Text(
                            text = item.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateTo(item.route) }
                )
            }
        }
    }
}

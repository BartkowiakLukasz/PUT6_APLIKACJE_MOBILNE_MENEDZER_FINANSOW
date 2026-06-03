package com.smartfinanse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.smartfinanse.presentation.theme.SmartFinanseTheme
import com.smartfinanse.presentation.transaction.list.TransactionListScreen
import dagger.hilt.android.AndroidEntryPoint

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartfinanse.presentation.dashboard.DashboardScreen
import com.smartfinanse.presentation.transaction.add.AddTransactionScreen
import android.net.Uri

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

import com.smartfinanse.presentation.category.CategoryManagementScreen
import com.smartfinanse.presentation.scanner.ScannerScreen
import com.smartfinanse.presentation.export.ExportScreen
import com.smartfinanse.presentation.settings.SettingsScreen

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartfinanse.domain.repository.AppTheme
import com.smartfinanse.domain.repository.UserPreferencesRepository
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val theme by preferencesRepository.theme.collectAsStateWithLifecycle()
            val currency by preferencesRepository.currency.collectAsStateWithLifecycle()
            
            com.smartfinanse.presentation.common.MoneyFormatter.currentCurrencySymbol = currency.symbol
            
            val isDarkTheme = when(theme) {
                AppTheme.SYSTEM -> isSystemInDarkTheme()
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
            }

            SmartFinanseTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            Spacer(Modifier.height(16.dp))
                            
                            val navigateTo = { route: String ->
                                scope.launch { drawerState.close() }
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }

                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                                label = { Text("Pulpit") },
                                selected = false,
                                onClick = { navigateTo("dashboard") },
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.List, contentDescription = null) },
                                label = { Text("Historia Transakcji") },
                                selected = false,
                                onClick = { navigateTo("history") },
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Build, contentDescription = null) },
                                label = { Text("Zarządzanie Kategoriami") },
                                selected = false,
                                onClick = { navigateTo("categories") },
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Search, contentDescription = null) },
                                label = { Text("Skaner Paragonów") },
                                selected = false,
                                onClick = { navigateTo("scanner") },
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Share, contentDescription = null) },
                                label = { Text("Eksport Danych") },
                                selected = false,
                                onClick = { navigateTo("export") },
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                label = { Text("Ustawienia") },
                                selected = false,
                                onClick = { navigateTo("settings") },
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        }
                    }
                ) {
                    NavHost(navController = navController, startDestination = "dashboard") {
                        composable("dashboard") {
                            DashboardScreen(
                                onNavigateToHistory = { navController.navigate("history") },
                                onNavigateToAdd = { navController.navigate("add") },
                                onOpenDrawer = { scope.launch { drawerState.open() } }
                            )
                        }
                        composable("history") {
                            TransactionListScreen(
                                onNavigateToAdd = { navController.navigate("add") },
                                onOpenDrawer = { scope.launch { drawerState.open() } }
                            )
                        }
                        composable("add?amount={amount}&description={description}&date={date}&categoryId={categoryId}") {
                            AddTransactionScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToScanner = { navController.navigate("scanner") }
                            )
                        }
                        composable("categories") {
                            CategoryManagementScreen(
                                onOpenDrawer = { scope.launch { drawerState.open() } }
                            )
                        }
                        composable("scanner") {
                            ScannerScreen(
                                onOpenDrawer = { scope.launch { drawerState.open() } },
                                onNavigateToAddWithPreFill = { amount, description, date, categoryId ->
                                    val amountStr = amount.toString()
                                    val descStr = Uri.encode(description)
                                    val dateStr = Uri.encode(date ?: "null")
                                    val catIdStr = categoryId?.toString() ?: "null"
                                    navController.navigate("add?amount=$amountStr&description=$descStr&date=$dateStr&categoryId=$catIdStr") {
                                        popUpTo("scanner") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("export") {
                            ExportScreen(
                                onOpenDrawer = { scope.launch { drawerState.open() } },
                                onNavigateToDashboard = { 
                                    navController.navigate("dashboard") {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                onOpenDrawer = { scope.launch { drawerState.open() } }
                            )
                        }
                    }
                }
            }
        }
    }
}

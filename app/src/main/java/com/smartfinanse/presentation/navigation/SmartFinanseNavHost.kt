package com.smartfinanse.presentation.navigation

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smartfinanse.presentation.category.CategoryManagementScreen
import com.smartfinanse.presentation.dashboard.DashboardScreen
import com.smartfinanse.presentation.export.ExportScreen
import com.smartfinanse.presentation.more.MoreScreen
import com.smartfinanse.presentation.scanner.ScannerScreen
import com.smartfinanse.presentation.settings.SettingsScreen
import com.smartfinanse.presentation.transaction.add.AddTransactionScreen
import com.smartfinanse.presentation.transaction.list.TransactionListScreen

@Composable
fun SmartFinanseNavHost(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val selectedBottomItem = BottomNavItem.fromRoute(currentRoute)
    val showBottomBar = BottomNavItem.showsBottomBar(currentRoute)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.DASHBOARD.route,
            modifier = Modifier.weight(1f)
        ) {
            composable(BottomNavItem.DASHBOARD.route) {
                DashboardScreen(
                    onNavigateToAdd = { navController.navigateSecondary("add") }
                )
            }
            composable(BottomNavItem.HISTORY.route) {
                TransactionListScreen(
                    onNavigateToAdd = { navController.navigateSecondary("add") }
                )
            }
            composable(BottomNavItem.MORE.route) {
                MoreScreen(
                    onNavigateTo = { route -> navController.navigateSecondary(route) }
                )
            }
            composable("add") {
                AddTransactionScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToScanner = { navController.navigateSecondary("scanner") }
                )
            }
            composable("add?amount={amount}&description={description}&date={date}&categoryId={categoryId}&isCash={isCash}") {
                AddTransactionScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToScanner = { navController.navigateSecondary("scanner") }
                )
            }
            composable("categories") {
                CategoryManagementScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("scanner") {
                ScannerScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAddWithPreFill = { amount, description, date, categoryId, isCash ->
                        val amountStr = amount.toString()
                        val descStr = Uri.encode(description)
                        val dateStr = Uri.encode(date ?: "null")
                        val catIdStr = categoryId?.toString() ?: "null"
                        val isCashStr = isCash?.toString() ?: "null"
                        navController.navigate(
                            "add?amount=$amountStr&description=$descStr&date=$dateStr&categoryId=$catIdStr&isCash=$isCashStr"
                        ) {
                            popUpTo("scanner") { inclusive = true }
                        }
                    }
                )
            }
            composable("export") {
                ExportScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("settings") {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        if (showBottomBar) {
            NavigationBar(
                modifier = Modifier.navigationBarsPadding(),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                BottomNavItem.all.forEach { item ->
                    NavigationBarItem(
                        selected = selectedBottomItem == item,
                        onClick = { navController.navigateTopLevel(item.route) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) },
                        alwaysShowLabel = true
                    )
                }
            }
        }
    }
}

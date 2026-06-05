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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.smartfinanse.presentation.category.CategoryManagementScreen
import com.smartfinanse.presentation.dashboard.DashboardChartsScreen
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
                    onNavigateToAddExpense = { navController.navigateSecondary("add/expense") },
                    onNavigateToAddIncome = { navController.navigateSecondary("add/income") },
                    onNavigateToCharts = { navController.navigateSecondary("dashboard/charts") }
                )
            }
            composable("dashboard/charts") {
                val parentEntry = remember {
                    navController.getBackStackEntry(BottomNavItem.DASHBOARD.route)
                }
                DashboardChartsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = hiltViewModel(parentEntry)
                )
            }
            composable(BottomNavItem.HISTORY.route) {
                TransactionListScreen(
                    onNavigateToAddExpense = { navController.navigateSecondary("add/expense") },
                    onNavigateToAddIncome = { navController.navigateSecondary("add/income") }
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
            composable(
                route = "add/{transactionType}?amount={amount}&description={description}&date={date}&categoryId={categoryId}&isCash={isCash}",
                arguments = listOf(
                    navArgument("transactionType") { type = NavType.StringType },
                    navArgument("amount") { type = NavType.StringType; defaultValue = "" },
                    navArgument("description") { type = NavType.StringType; defaultValue = "" },
                    navArgument("date") { type = NavType.StringType; defaultValue = "" },
                    navArgument("categoryId") { type = NavType.StringType; defaultValue = "" },
                    navArgument("isCash") { type = NavType.StringType; defaultValue = "" }
                )
            ) {
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
                            "add/expense?amount=$amountStr&description=$descStr&date=$dateStr&categoryId=$catIdStr&isCash=$isCashStr"
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

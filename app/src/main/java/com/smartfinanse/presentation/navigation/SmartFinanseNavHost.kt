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
import com.smartfinanse.presentation.category.add.AddCategoryScreen
import com.smartfinanse.presentation.dashboard.DashboardChartsScreen
import com.smartfinanse.presentation.dashboard.DashboardScreen
import com.smartfinanse.presentation.export.ExportScreen
import com.smartfinanse.presentation.more.MoreScreen
import com.smartfinanse.presentation.scanner.ScannerScreen
import com.smartfinanse.presentation.settings.SettingsScreen
import com.smartfinanse.presentation.subscriptions.SubscriptionsScreen
import com.smartfinanse.presentation.store.add.AddStoreScreen
import com.smartfinanse.presentation.store.management.StoreManagementScreen
import com.smartfinanse.presentation.transaction.add.AddTransactionScreen
import com.smartfinanse.presentation.transaction.list.TransactionListScreen
import com.smartfinanse.presentation.subscription.add.AddSubscriptionScreen

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
                    onNavigateToAddSubscription = { navController.navigateSecondary("add/subscription") },
                    onNavigateToCharts = { navController.navigateSecondary("dashboard/charts") }
                )
            }
            composable("dashboard/charts") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(BottomNavItem.DASHBOARD.route)
                }
                DashboardChartsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = hiltViewModel(parentEntry)
                )
            }
            composable("subscriptions") {
                SubscriptionsScreen(
                    onNavigateToAddSubscription = { navController.navigateSecondary("add/subscription") }
                )
            }
            composable(BottomNavItem.HISTORY.route) {
                TransactionListScreen(
                    onNavigateToAddExpense = { navController.navigateSecondary("add/expense") },
                    onNavigateToAddIncome = { navController.navigateSecondary("add/income") },
                    onNavigateToAddSubscription = { navController.navigateSecondary("add/subscription") }
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
                    onNavigateToScanner = { navController.navigateSecondary("scanner") },
                    onNavigateToCategoryAdd = { isExpense ->
                        navController.navigateSecondary("addCategory/$isExpense")
                    },
                    onNavigateToStoreAdd = {
                        navController.navigateSecondary("addStore")
                    }
                )
            }
            composable("add/subscription") {
                AddSubscriptionScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(
                route = "add/{transactionType}?amount={amount}&description={description}&storeName={storeName}&storeId={storeId}&date={date}&categoryId={categoryId}&isCash={isCash}&isFallbackCategory={isFallbackCategory}",
                arguments = listOf(
                    navArgument("transactionType") { type = NavType.StringType },
                    navArgument("amount") { type = NavType.StringType; nullable = true; defaultValue = null },
                    navArgument("description") { type = NavType.StringType; nullable = true; defaultValue = null },
                    navArgument("storeName") { type = NavType.StringType; nullable = true; defaultValue = null },
                    navArgument("storeId") { type = NavType.StringType; nullable = true; defaultValue = null },
                    navArgument("date") { type = NavType.StringType; nullable = true; defaultValue = null },
                    navArgument("categoryId") { type = NavType.StringType; nullable = true; defaultValue = null },
                    navArgument("isCash") { type = NavType.StringType; nullable = true; defaultValue = null },
                    navArgument("isFallbackCategory") { type = NavType.StringType; nullable = true; defaultValue = null }
                )
            ) {
                AddTransactionScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToScanner = { navController.navigateSecondary("scanner") },
                    onNavigateToCategoryAdd = { isExpense ->
                        navController.navigateSecondary("addCategory/$isExpense")
                    },
                    onNavigateToStoreAdd = {
                        navController.navigateSecondary("addStore")
                    }
                )
            }
            composable(
                route = "addCategory/{isExpense}",
                arguments = listOf(navArgument("isExpense") { type = NavType.BoolType })
            ) {
                AddCategoryScreen(
                    onNavigateBackWithResult = { categoryId ->
                        if (categoryId != null) {
                            navController.previousBackStackEntry?.savedStateHandle?.set("newCategoryId", categoryId)
                        }
                        navController.popBackStack()
                    }
                )
            }
            composable("addStore") {
                AddStoreScreen(
                    onNavigateBackWithResult = { storeId ->
                        if (storeId != null) {
                            navController.previousBackStackEntry?.savedStateHandle?.set("newStoreId", storeId)
                        }
                        navController.popBackStack()
                    }
                )
            }
            composable("categories") {
                CategoryManagementScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("stores") {
                StoreManagementScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAddStore = { navController.navigateSecondary("addStore") }
                )
            }
            composable("scanner") {
                ScannerScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAddWithPreFill = { amount, description, storeName, storeId, date, categoryId, isCash, isFallbackCategory ->
                        val amountStr = amount.toString()
                        val descStr = Uri.encode(description)
                        val storeNameStr = Uri.encode(storeName)
                        val storeIdStr = storeId?.toString() ?: "null"
                        val dateStr = Uri.encode(date ?: "null")
                        val catIdStr = categoryId?.toString() ?: "null"
                        val isCashStr = isCash?.toString() ?: "null"
                        val isFallbackStr = isFallbackCategory.toString()
                        navController.navigate(
                            "add/expense?amount=$amountStr&description=$descStr&storeName=$storeNameStr&storeId=$storeIdStr&date=$dateStr&categoryId=$catIdStr&isCash=$isCashStr&isFallbackCategory=$isFallbackStr"
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
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToChangePin = { navController.navigateSecondary("changePin") }
                )
            }
            composable("changePin") {
                val authViewModel: com.smartfinanse.presentation.auth.AuthViewModel = hiltViewModel()
                com.smartfinanse.presentation.auth.ChangePinScreen(
                    onVerifyOldPin = { pin, onResult -> authViewModel.verifyPin(pin, onResult) },
                    onPinSetup = { pin -> 
                        authViewModel.setupPin(pin)
                        navController.popBackStack()
                    }
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

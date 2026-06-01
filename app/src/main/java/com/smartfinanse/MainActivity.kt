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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartFinanseTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "dashboard") {
                    composable("dashboard") {
                        DashboardScreen(
                            onNavigateToHistory = { navController.navigate("history") },
                            onNavigateToAdd = { navController.navigate("add") }
                        )
                    }
                    composable("history") {
                        TransactionListScreen(
                            onNavigateToAdd = { navController.navigate("add") }
                        )
                    }
                    composable("add") {
                        AddTransactionScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

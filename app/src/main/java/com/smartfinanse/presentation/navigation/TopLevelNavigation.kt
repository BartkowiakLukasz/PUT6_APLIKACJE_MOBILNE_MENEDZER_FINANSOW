package com.smartfinanse.presentation.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

/**
 * Navigate between bottom-nav root destinations without breaking the back stack.
 */
fun NavHostController.navigateTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

/** Push a secondary screen (from Więcej, FAB, etc.) */
fun NavHostController.navigateSecondary(route: String) {
    navigate(route) {
        launchSingleTop = true
    }
}

package com.smartfinanse.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    DASHBOARD("dashboard", "Pulpit", Icons.Default.Home),
    HISTORY("history", "Historia", Icons.Default.List),
    SUBSCRIPTIONS("subscriptions", "Subskrypcje", Icons.Default.Autorenew),
    MORE("more", "Więcej", Icons.Default.MoreHoriz);

    companion object {
        val all = listOf(DASHBOARD, HISTORY, SUBSCRIPTIONS, MORE)

        fun fromRoute(route: String?): BottomNavItem? = when {
            route == null -> null
            route == DASHBOARD.route -> DASHBOARD
            route == HISTORY.route -> HISTORY
            route == SUBSCRIPTIONS.route -> SUBSCRIPTIONS
            route == MORE.route -> MORE
            route in secondaryMoreRoutes -> MORE
            else -> null
        }

        private val secondaryMoreRoutes = setOf(
            "categories",
            "scanner",
            "export",
            "settings"
        )

        fun showsBottomBar(route: String?): Boolean {
            if (route == null) return false
            if (route.startsWith("add")) return false
            if (route.startsWith("dashboard/")) return false
            return fromRoute(route) != null
        }
    }
}

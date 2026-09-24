package com.ecotrack.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Category
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object Categories : Screen("categories")
    object Logs : Screen("logs")
    object Settings : Screen("settings")
}

data class BottomNavItem(val screen: Screen, val label: String, val icon: ImageVector)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Dashboard, "Dashboard", Icons.Default.Dashboard),
    BottomNavItem(Screen.Categories, "Categories", Icons.Default.Category),
    BottomNavItem(Screen.Logs, "Logs", Icons.Default.List),
    BottomNavItem(Screen.Settings, "Settings", Icons.Default.Settings)
)

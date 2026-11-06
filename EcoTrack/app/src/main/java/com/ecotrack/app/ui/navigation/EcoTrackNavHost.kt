package com.ecotrack.app.ui.navigation

import androidx.compose.material3.*
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ecotrack.app.ui.screens.auth.LoginScreen
import com.ecotrack.app.ui.screens.auth.RegisterScreen
import com.ecotrack.app.ui.screens.categories.CategoriesScreen
import com.ecotrack.app.ui.screens.dashboard.DashboardScreen
import com.ecotrack.app.ui.screens.logs.LogsScreen
import com.ecotrack.app.ui.screens.settings.SettingsScreen
import com.ecotrack.app.viewmodel.ViewModelFactory

private const val MAIN_GRAPH_ROUTE = "main"

@Composable
fun EcoTrackNavHost(factory: ViewModelFactory, startLoggedIn: Boolean) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = bottomNavItems.any { it.screen.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.screen.route,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = if (startLoggedIn) Screen.Dashboard.route else Screen.Login.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    factory = factory,
                    onLoginSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    factory = factory,
                    onRegisterSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }
            composable(Screen.Dashboard.route) { DashboardScreen(factory) }
            composable(Screen.Categories.route) { CategoriesScreen(factory) }
            composable(Screen.Logs.route) { LogsScreen(factory) }
            composable(Screen.Settings.route) {
                SettingsScreen(factory) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) // clear the whole back stack on logout
                    }
                }
            }
        }
    }
}


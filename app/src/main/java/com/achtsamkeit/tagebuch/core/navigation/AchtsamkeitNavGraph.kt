package com.achtsamkeit.tagebuch.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.achtsamkeit.tagebuch.presentation.archive.ArchiveScreen
import com.achtsamkeit.tagebuch.presentation.home.HomeScreen
import com.achtsamkeit.tagebuch.presentation.settings.SettingsScreen

@Composable
fun AchtsamkeitNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController   = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Archive.route) {
            ArchiveScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(Screen.CreateEntry.route) {
            // Wird in Phase 2 implementiert
        }
    }
}
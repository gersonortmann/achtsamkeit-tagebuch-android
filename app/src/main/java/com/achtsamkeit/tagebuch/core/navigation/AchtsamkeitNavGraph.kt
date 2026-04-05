package com.achtsamkeit.tagebuch.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.achtsamkeit.tagebuch.presentation.archive.ArchiveScreen
import com.achtsamkeit.tagebuch.presentation.entry.CreateEntryScreen
import com.achtsamkeit.tagebuch.presentation.entry.EntryDetailScreen
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
        composable(
            route = Screen.CreateEntry.route,
            arguments = listOf(navArgument("entryId") { type = NavType.LongType; defaultValue = -1L })
        ) {
            CreateEntryScreen(navController = navController)
        }
        composable(
            route = Screen.EntryDetail.route,
            arguments = listOf(navArgument("entryId") { type = NavType.LongType })
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getLong("entryId") ?: -1L
            EntryDetailScreen(
                entryId = entryId,
                navController = navController
            )
        }
    }
}

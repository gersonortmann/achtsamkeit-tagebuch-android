package com.achtsamkeit.tagebuch.core.navigation

/**
 * Alle Navigations-Routen der App zentral definiert.
 * Neue Screens hier eintragen, nie Magic Strings im Code verwenden.
 */
sealed class Screen(val route: String) {
    object Home        : Screen("home")
    object Archive     : Screen("archive")
    object Settings    : Screen("settings")
    object CreateEntry : Screen("create_entry")
    object EntryDetail : Screen("entry_detail/{entryId}") {
        fun createRoute(entryId: Long) = "entry_detail/$entryId"
    }
}
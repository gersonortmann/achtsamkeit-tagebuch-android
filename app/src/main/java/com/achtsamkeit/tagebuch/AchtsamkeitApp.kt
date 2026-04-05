package com.achtsamkeit.tagebuch

import android.app.Application
import com.achtsamkeit.tagebuch.data.local.database.DatabaseInitializer
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Application-Einstiegspunkt.
 * @HiltAndroidApp aktiviert Hilt für das gesamte Projekt.
 */
@HiltAndroidApp
class AchtsamkeitApp : Application() {

    @Inject
    lateinit var databaseInitializer: DatabaseInitializer

    override fun onCreate() {
        super.onCreate()
        
        // Datenbank initialisieren, falls nötig
        MainScope().launch {
            databaseInitializer.initializeIfNeeded()
        }
    }
}
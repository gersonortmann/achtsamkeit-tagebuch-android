package com.achtsamkeit.tagebuch

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application-Einstiegspunkt.
 * @HiltAndroidApp aktiviert Hilt für das gesamte Projekt.
 */
@HiltAndroidApp
class AchtsamkeitApp : Application()
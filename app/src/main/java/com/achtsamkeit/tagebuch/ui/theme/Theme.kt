package com.achtsamkeit.tagebuch.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.achtsamkeit.tagebuch.domain.model.ThemeConfig

private val LightColorScheme = lightColorScheme()
private val DarkColorScheme  = darkColorScheme()

@Composable
fun AchtsamkeitTheme(
    themeConfig:  ThemeConfig = ThemeConfig.FOLLOW_SYSTEM,
    dynamicColor: Boolean = true,           // Material You – Wallpaper-Farben
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeConfig) {
        ThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        ThemeConfig.LIGHT         -> false
        ThemeConfig.DARK          -> true
    }

    val colorScheme = when {
        // Dynamische Farben aus dem Wallpaper (minSdk=31, daher immer verfügbar)
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else           dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content     = content
    )
}

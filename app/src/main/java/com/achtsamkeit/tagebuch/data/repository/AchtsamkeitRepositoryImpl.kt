package com.achtsamkeit.tagebuch.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.achtsamkeit.tagebuch.domain.model.ThemeConfig
import com.achtsamkeit.tagebuch.domain.repository.AchtsamkeitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AchtsamkeitRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AchtsamkeitRepository {

    private object PreferencesKeys {
        val THEME_CONFIG = stringPreferencesKey("theme_config")
    }

    override val themeConfig: Flow<ThemeConfig> = dataStore.data
        .map { preferences ->
            val themeName = preferences[PreferencesKeys.THEME_CONFIG] ?: ThemeConfig.FOLLOW_SYSTEM.name
            try {
                ThemeConfig.valueOf(themeName)
            } catch (_: Exception) {
                ThemeConfig.FOLLOW_SYSTEM
            }
        }

    override suspend fun setThemeConfig(config: ThemeConfig) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_CONFIG] = config.name
        }
    }
}

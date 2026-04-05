package com.achtsamkeit.tagebuch.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
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
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
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

    override val reminderEnabled: Flow<Boolean> = dataStore.data
        .map { it[PreferencesKeys.REMINDER_ENABLED] ?: false }

    override val reminderHour: Flow<Int> = dataStore.data
        .map { it[PreferencesKeys.REMINDER_HOUR] ?: 20 }

    override val reminderMinute: Flow<Int> = dataStore.data
        .map { it[PreferencesKeys.REMINDER_MINUTE] ?: 0 }

    override suspend fun setReminderEnabled(enabled: Boolean) {
        dataStore.edit { it[PreferencesKeys.REMINDER_ENABLED] = enabled }
    }

    override suspend fun setReminderTime(hour: Int, minute: Int) {
        dataStore.edit {
            it[PreferencesKeys.REMINDER_HOUR] = hour
            it[PreferencesKeys.REMINDER_MINUTE] = minute
        }
    }
}

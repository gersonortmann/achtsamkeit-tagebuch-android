package com.achtsamkeit.tagebuch.domain.repository

import com.achtsamkeit.tagebuch.domain.model.ThemeConfig
import kotlinx.coroutines.flow.Flow

interface AchtsamkeitRepository {
    val themeConfig: Flow<ThemeConfig>
    suspend fun setThemeConfig(config: ThemeConfig)

    val reminderEnabled: Flow<Boolean>
    val reminderHour: Flow<Int>
    val reminderMinute: Flow<Int>
    suspend fun setReminderEnabled(enabled: Boolean)
    suspend fun setReminderTime(hour: Int, minute: Int)
}

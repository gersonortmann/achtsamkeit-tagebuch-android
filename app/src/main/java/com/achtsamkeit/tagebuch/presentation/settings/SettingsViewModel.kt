package com.achtsamkeit.tagebuch.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.achtsamkeit.tagebuch.domain.model.ThemeConfig
import com.achtsamkeit.tagebuch.domain.repository.AchtsamkeitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: AchtsamkeitRepository
) : ViewModel() {

    val themeConfig: StateFlow<ThemeConfig> = repository.themeConfig
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeConfig.FOLLOW_SYSTEM
        )

    fun setThemeConfig(config: ThemeConfig) {
        viewModelScope.launch {
            repository.setThemeConfig(config)
        }
    }
}

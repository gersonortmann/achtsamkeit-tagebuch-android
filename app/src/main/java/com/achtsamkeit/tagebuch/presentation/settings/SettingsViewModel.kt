package com.achtsamkeit.tagebuch.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.achtsamkeit.tagebuch.domain.model.GuidedQuestion
import com.achtsamkeit.tagebuch.domain.model.ThemeConfig
import com.achtsamkeit.tagebuch.domain.repository.AchtsamkeitRepository
import com.achtsamkeit.tagebuch.domain.repository.JournalRepository
import com.achtsamkeit.tagebuch.domain.repository.SecurityRepository
import com.achtsamkeit.tagebuch.domain.usecase.GetAllQuestionsUseCase
import com.achtsamkeit.tagebuch.domain.usecase.UpdateQuestionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: AchtsamkeitRepository,
    private val securityRepository: SecurityRepository,
    private val journalRepository: JournalRepository,
    getAllQuestionsUseCase: GetAllQuestionsUseCase,
    private val updateQuestionUseCase: UpdateQuestionUseCase
) : ViewModel() {

    val themeConfig: StateFlow<ThemeConfig> = repository.themeConfig
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeConfig.FOLLOW_SYSTEM
        )

    val questions: StateFlow<List<GuidedQuestion>> = getAllQuestionsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val isBiometricEnabled: StateFlow<Boolean> = securityRepository.isBiometricEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val reminderEnabled: StateFlow<Boolean> = repository.reminderEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val reminderHour: StateFlow<Int> = repository.reminderHour
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 20
        )

    val reminderMinute: StateFlow<Int> = repository.reminderMinute
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    fun setThemeConfig(config: ThemeConfig) {
        viewModelScope.launch { repository.setThemeConfig(config) }
    }

    fun toggleQuestionSelection(question: GuidedQuestion) {
        viewModelScope.launch {
            updateQuestionUseCase(question.copy(isSelected = !question.isSelected))
        }
    }

    fun addQuestion(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            journalRepository.insertQuestion(
                GuidedQuestion(question = text.trim(), isDefault = false, isSelected = true)
            )
        }
    }

    fun deleteQuestion(question: GuidedQuestion) {
        viewModelScope.launch { journalRepository.deleteQuestion(question) }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch { securityRepository.setBiometricEnabled(enabled) }
    }

    fun setReminderEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setReminderEnabled(enabled) }
    }

    fun setReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch { repository.setReminderTime(hour, minute) }
    }
}

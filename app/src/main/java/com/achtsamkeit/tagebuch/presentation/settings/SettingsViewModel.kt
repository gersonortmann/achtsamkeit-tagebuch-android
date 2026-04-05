package com.achtsamkeit.tagebuch.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.achtsamkeit.tagebuch.domain.model.GuidedQuestion
import com.achtsamkeit.tagebuch.domain.model.ThemeConfig
import com.achtsamkeit.tagebuch.domain.repository.AchtsamkeitRepository
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

    fun setThemeConfig(config: ThemeConfig) {
        viewModelScope.launch {
            repository.setThemeConfig(config)
        }
    }

    fun toggleQuestionSelection(question: GuidedQuestion) {
        viewModelScope.launch {
            updateQuestionUseCase(question.copy(isSelected = !question.isSelected))
        }
    }
}

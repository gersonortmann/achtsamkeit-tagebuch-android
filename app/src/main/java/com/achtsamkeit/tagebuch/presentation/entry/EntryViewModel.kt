package com.achtsamkeit.tagebuch.presentation.entry

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.achtsamkeit.tagebuch.domain.model.GuidedAnswer
import com.achtsamkeit.tagebuch.domain.model.JournalEntry
import com.achtsamkeit.tagebuch.domain.model.MoodLevel
import com.achtsamkeit.tagebuch.domain.usecase.GetEntryByIdUseCase
import com.achtsamkeit.tagebuch.domain.usecase.GetSelectedQuestionsUseCase
import com.achtsamkeit.tagebuch.domain.usecase.SaveEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val getSelectedQuestionsUseCase: GetSelectedQuestionsUseCase,
    private val saveEntryUseCase: SaveEntryUseCase,
    private val getEntryByIdUseCase: GetEntryByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val entryId: Long? = savedStateHandle.get<Long>("entryId")

    private val _uiState = MutableStateFlow(EntryUiState())
    val uiState: StateFlow<EntryUiState> = _uiState.asStateFlow()

    init {
        if (entryId != null && entryId != -1L) {
            loadExistingEntry(entryId)
        } else {
            loadSelectedQuestions()
        }
    }

    private fun loadExistingEntry(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) } // Benutze isSaving als Lade-Indikator
            val entry = getEntryByIdUseCase(id)
            if (entry != null) {
                _uiState.update { state ->
                    state.copy(
                        entryDate = entry.date,
                        moodLevel = MoodLevel.fromScore(entry.moodScore),
                        freeText = entry.freeText,
                        gratitudeItems = entry.gratitudeItems + List(3 - entry.gratitudeItems.size) { "" },
                        guidedAnswers = entry.guidedAnswers,
                        tags = entry.tags,
                        isSaving = false
                    )
                }
            } else {
                _uiState.update { it.copy(isSaving = false, error = "Eintrag nicht gefunden.") }
            }
        }
    }

    private fun loadSelectedQuestions() {
        viewModelScope.launch {
            val questions = getSelectedQuestionsUseCase().first()
            _uiState.update { state ->
                state.copy(
                    guidedAnswers = questions.map {
                        GuidedAnswer(question = it.question, answer = "")
                    }
                )
            }
        }
    }

    fun onMoodChanged(newMood: MoodLevel) {
        _uiState.update { it.copy(moodLevel = newMood) }
    }

    fun onFreeTextChanged(newText: String) {
        _uiState.update { it.copy(freeText = newText) }
    }

    fun onGuidedAnswerChanged(question: String, newAnswer: String) {
        _uiState.update { state ->
            val newList = state.guidedAnswers.map {
                if (it.question == question) it.copy(answer = newAnswer) else it
            }
            state.copy(guidedAnswers = newList)
        }
    }

    fun onGratitudeItemChanged(index: Int, newItem: String) {
        _uiState.update { state ->
            val newList = state.gratitudeItems.toMutableList()
            if (index in newList.indices) {
                newList[index] = newItem
            }
            state.copy(gratitudeItems = newList.toList())
        }
    }

    fun onDateChanged(date: LocalDate) {
        _uiState.update { it.copy(entryDate = date) }
    }

    fun onTagAdded(tag: String) {
        if (tag.isBlank()) return
        _uiState.update { state ->
            if (state.tags.contains(tag)) return@update state
            state.copy(tags = state.tags + tag)
        }
    }

    fun onTagRemoved(tag: String) {
        _uiState.update { state ->
            state.copy(tags = state.tags - tag)
        }
    }

    fun saveEntry() {
        val currentState = _uiState.value
        if (currentState.freeText.isBlank() &&
            currentState.guidedAnswers.all { it.answer.isBlank() } &&
            currentState.gratitudeItems.all { it.isBlank() }
        ) {
            _uiState.update { it.copy(error = "Bitte schreibe etwas in dein Tagebuch.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val entry = JournalEntry(
                    id = entryId ?: 0L,
                    date = currentState.entryDate,
                    createdAt = LocalDateTime.now(),
                    moodScore = currentState.moodLevel.score,
                    moodEmoji = currentState.moodLevel.emoji,
                    freeText = currentState.freeText,
                    gratitudeItems = currentState.gratitudeItems.filter { it.isNotBlank() },
                    guidedAnswers = currentState.guidedAnswers,
                    tags = currentState.tags
                )
                saveEntryUseCase(entry)
                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = e.localizedMessage) }
            }
        }
    }
}

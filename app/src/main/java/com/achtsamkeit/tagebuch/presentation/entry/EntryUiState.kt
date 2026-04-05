package com.achtsamkeit.tagebuch.presentation.entry

import com.achtsamkeit.tagebuch.domain.model.GuidedAnswer
import com.achtsamkeit.tagebuch.domain.model.MoodLevel
import java.time.LocalDate
import java.time.LocalDateTime

data class EntryUiState(
    val entryDate: LocalDate = LocalDate.now(),
    val createdAt: LocalDateTime? = null,
    val moodLevel: MoodLevel = MoodLevel.NEUTRAL,
    val freeText: String = "",
    val guidedAnswers: List<GuidedAnswer> = emptyList(),
    val gratitudeItems: List<String> = listOf("", "", ""),
    val labels: List<String> = emptyList(),
    val isEditMode: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

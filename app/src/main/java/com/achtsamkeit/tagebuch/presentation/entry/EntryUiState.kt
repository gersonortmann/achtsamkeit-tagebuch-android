package com.achtsamkeit.tagebuch.presentation.entry

import com.achtsamkeit.tagebuch.domain.model.GuidedAnswer
import com.achtsamkeit.tagebuch.domain.model.MoodLevel
import java.time.LocalDate

data class EntryUiState(
    val entryDate: LocalDate = LocalDate.now(),
    val moodLevel: MoodLevel = MoodLevel.NEUTRAL,
    val freeText: String = "",
    val guidedAnswers: List<GuidedAnswer> = emptyList(),
    val gratitudeItems: List<String> = listOf("", "", ""),
    val tags: List<String> = emptyList(),
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

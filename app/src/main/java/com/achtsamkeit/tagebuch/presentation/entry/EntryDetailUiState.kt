package com.achtsamkeit.tagebuch.presentation.entry

import com.achtsamkeit.tagebuch.domain.model.JournalEntry

data class EntryDetailUiState(
    val entry: JournalEntry? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

package com.achtsamkeit.tagebuch.presentation.archive

import com.achtsamkeit.tagebuch.domain.model.JournalEntry

data class ArchiveUiState(
    val entries: List<JournalEntry> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val showLabelsInline: Boolean = false
)

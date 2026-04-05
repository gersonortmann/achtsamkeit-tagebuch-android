package com.achtsamkeit.tagebuch.presentation.archive

import com.achtsamkeit.tagebuch.domain.model.JournalEntry
import java.time.LocalDate

data class FilterState(
    val selectedMoodScores: Set<Int> = emptySet(),
    val dateFrom: LocalDate? = null,
    val dateTo: LocalDate? = null,
    val selectedLabels: Set<String> = emptySet()
) {
    val isActive: Boolean
        get() =
            selectedMoodScores.isNotEmpty() || dateFrom != null ||
                    dateTo != null || selectedLabels.isNotEmpty()
}

data class ArchiveUiState(
    val entries: List<JournalEntry> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val showLabelsInline: Boolean = false,
    val filterState: FilterState = FilterState(),
    val availableLabels: List<String> = emptyList(),
    val deletedEntry: JournalEntry? = null,
    val showFilterSheet: Boolean = false
)

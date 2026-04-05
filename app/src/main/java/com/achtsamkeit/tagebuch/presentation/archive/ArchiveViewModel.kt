package com.achtsamkeit.tagebuch.presentation.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.achtsamkeit.tagebuch.domain.model.JournalEntry
import com.achtsamkeit.tagebuch.domain.usecase.DeleteEntryUseCase
import com.achtsamkeit.tagebuch.domain.usecase.GetEntriesUseCase
import com.achtsamkeit.tagebuch.domain.usecase.SaveEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    getEntriesUseCase: GetEntriesUseCase,
    private val deleteEntryUseCase: DeleteEntryUseCase,
    private val saveEntryUseCase: SaveEntryUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _showLabelsInline = MutableStateFlow(false)
    private val _deletedEntry = MutableStateFlow<JournalEntry?>(null)
    private val _filterState = MutableStateFlow(FilterState())
    private val _showFilterSheet = MutableStateFlow(false)

    private val _allEntries = getEntriesUseCase()

    val uiState: StateFlow<ArchiveUiState> = combine(
        _allEntries,
        _searchQuery,
        _showLabelsInline,
        _deletedEntry,
        _filterState,
        _showFilterSheet
    ) { params: Array<Any?> ->
        val entries = (params[0] as? List<*>)?.filterIsInstance<JournalEntry>() ?: emptyList()
        val query = params[1] as String
        val inlineLabels = params[2] as Boolean
        val deletedEntry = params[3] as JournalEntry?
        val filter = params[4] as FilterState
        val showSheet = params[5] as Boolean
        
        val filteredBySearch = if (query.isBlank()) {
            entries
        } else {
            entries.filter { entry ->
                entry.freeText.contains(query, ignoreCase = true) ||
                        entry.labels.any { it.contains(query, ignoreCase = true) } ||
                        entry.gratitudeItems.any { it.contains(query, ignoreCase = true) } ||
                        entry.guidedAnswers.any { it.answer.contains(query, ignoreCase = true) }
            }
        }

        val finalEntries = filteredBySearch.filter { entry ->
            val moodMatch = filter.selectedMoodScores.isEmpty() || entry.moodScore in filter.selectedMoodScores
            val dateFromMatch = filter.dateFrom == null || !entry.date.isBefore(filter.dateFrom)
            val dateToMatch = filter.dateTo == null || !entry.date.isAfter(filter.dateTo)
            val labelMatch = filter.selectedLabels.isEmpty() || entry.labels.any { it in filter.selectedLabels }
            
            moodMatch && dateFromMatch && dateToMatch && labelMatch
        }

        val availableLabels = entries.flatMap { it.labels }.distinct().sorted()

        ArchiveUiState(
            entries = finalEntries.sortedByDescending { it.date },
            searchQuery = query,
            showLabelsInline = inlineLabels,
            filterState = filter,
            availableLabels = availableLabels,
            deletedEntry = deletedEntry,
            showFilterSheet = showSheet
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ArchiveUiState(isLoading = true)
    )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun toggleLabelsDisplay() {
        _showLabelsInline.value = !_showLabelsInline.value
    }

    fun deleteEntry(entry: JournalEntry) {
        viewModelScope.launch {
            _deletedEntry.value = entry
            deleteEntryUseCase(entry)
        }
    }

    fun restoreEntry() {
        val entryToRestore = _deletedEntry.value ?: return
        viewModelScope.launch {
            saveEntryUseCase(entryToRestore)
            _deletedEntry.value = null
        }
    }

    fun clearDeletedEntry() {
        _deletedEntry.value = null
    }

    fun setShowFilterSheet(show: Boolean) {
        _showFilterSheet.value = show
    }

    fun onMoodFilterToggle(score: Int) {
        _filterState.update { state ->
            val newScores = if (score in state.selectedMoodScores) {
                state.selectedMoodScores - score
            } else {
                state.selectedMoodScores + score
            }
            state.copy(selectedMoodScores = newScores)
        }
    }

    fun onDateFromChange(date: LocalDate?) {
        _filterState.update { it.copy(dateFrom = date) }
    }

    fun onDateToChange(date: LocalDate?) {
        _filterState.update { it.copy(dateTo = date) }
    }

    fun onLabelFilterToggle(label: String) {
        _filterState.update { state ->
            val newLabels = if (label in state.selectedLabels) {
                state.selectedLabels - label
            } else {
                state.selectedLabels + label
            }
            state.copy(selectedLabels = newLabels)
        }
    }

    fun resetFilters() {
        _filterState.value = FilterState()
    }
}

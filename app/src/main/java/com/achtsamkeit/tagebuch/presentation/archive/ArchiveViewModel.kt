package com.achtsamkeit.tagebuch.presentation.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.achtsamkeit.tagebuch.domain.usecase.GetEntriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    getEntriesUseCase: GetEntriesUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _showLabelsInline = MutableStateFlow(false)

    val uiState: StateFlow<ArchiveUiState> = combine(
        getEntriesUseCase(),
        _searchQuery,
        _showLabelsInline
    ) { entries, query, inlineLabels ->
        val filteredEntries = if (query.isBlank()) {
            entries
        } else {
            entries.filter { entry ->
                entry.freeText.contains(query, ignoreCase = true) ||
                        entry.labels.any { it.contains(query, ignoreCase = true) } ||
                        entry.gratitudeItems.any { it.contains(query, ignoreCase = true) } ||
                        entry.guidedAnswers.any { it.answer.contains(query, ignoreCase = true) }
            }
        }
        ArchiveUiState(
            entries = filteredEntries.sortedByDescending { it.date },
            searchQuery = query,
            showLabelsInline = inlineLabels
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
}

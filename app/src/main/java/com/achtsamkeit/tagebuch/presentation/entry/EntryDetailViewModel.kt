package com.achtsamkeit.tagebuch.presentation.entry

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.achtsamkeit.tagebuch.domain.model.JournalEntry
import com.achtsamkeit.tagebuch.domain.usecase.GetEntryByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EntryDetailUiState(
    val entry: JournalEntry? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EntryDetailViewModel @Inject constructor(
    private val getEntryByIdUseCase: GetEntryByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val entryId: Long = checkNotNull(savedStateHandle["entryId"])

    private val _uiState = MutableStateFlow(EntryDetailUiState())
    val uiState: StateFlow<EntryDetailUiState> = _uiState.asStateFlow()

    init {
        loadEntry()
    }

    private fun loadEntry() {
        viewModelScope.launch {
            _uiState.value = EntryDetailUiState(isLoading = true)
            try {
                val entry = getEntryByIdUseCase(entryId)
                if (entry != null) {
                    _uiState.value = EntryDetailUiState(entry = entry)
                } else {
                    _uiState.value = EntryDetailUiState(error = "Eintrag nicht gefunden.")
                }
            } catch (e: Exception) {
                _uiState.value = EntryDetailUiState(error = e.localizedMessage)
            }
        }
    }
}

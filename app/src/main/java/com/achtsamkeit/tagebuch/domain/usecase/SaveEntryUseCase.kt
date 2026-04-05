package com.achtsamkeit.tagebuch.domain.usecase

import com.achtsamkeit.tagebuch.domain.model.JournalEntry
import com.achtsamkeit.tagebuch.domain.repository.JournalRepository
import javax.inject.Inject

class SaveEntryUseCase @Inject constructor(
    private val repository: JournalRepository
) {
    suspend operator fun invoke(entry: JournalEntry): Long {
        return if (entry.id == 0L) {
            repository.insertEntry(entry)
        } else {
            repository.updateEntry(entry)
            entry.id
        }
    }
}

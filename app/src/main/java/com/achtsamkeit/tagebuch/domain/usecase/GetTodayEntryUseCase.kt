package com.achtsamkeit.tagebuch.domain.usecase

import com.achtsamkeit.tagebuch.domain.model.JournalEntry
import com.achtsamkeit.tagebuch.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class GetTodayEntryUseCase @Inject constructor(
    private val repository: JournalRepository
) {
    operator fun invoke(): Flow<JournalEntry?> {
        val today = LocalDate.now()
        return repository.getAllEntries()
            .map { entries ->
                entries.find { it.date == today }
            }
    }
}

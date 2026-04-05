package com.achtsamkeit.tagebuch.domain.usecase

import com.achtsamkeit.tagebuch.domain.model.JournalEntry
import com.achtsamkeit.tagebuch.domain.repository.JournalRepository
import javax.inject.Inject

class GetEntryByIdUseCase @Inject constructor(
    private val repository: JournalRepository
) {
    suspend operator fun invoke(id: Long): JournalEntry? {
        return repository.getEntryById(id)
    }
}

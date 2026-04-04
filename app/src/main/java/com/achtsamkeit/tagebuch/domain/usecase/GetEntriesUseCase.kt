package com.achtsamkeit.tagebuch.domain.usecase

import com.achtsamkeit.tagebuch.domain.repository.AchtsamkeitRepository

class GetEntriesUseCase(
    private val repository: AchtsamkeitRepository
) {
    operator fun invoke() {
        // Logik für den Use Case
    }
}

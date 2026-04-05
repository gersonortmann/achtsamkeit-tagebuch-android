package com.achtsamkeit.tagebuch.domain.usecase

import com.achtsamkeit.tagebuch.domain.model.GuidedQuestion
import com.achtsamkeit.tagebuch.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSelectedQuestionsUseCase @Inject constructor(
    private val repository: JournalRepository
) {
    operator fun invoke(): Flow<List<GuidedQuestion>> = repository.getSelectedQuestions()
}

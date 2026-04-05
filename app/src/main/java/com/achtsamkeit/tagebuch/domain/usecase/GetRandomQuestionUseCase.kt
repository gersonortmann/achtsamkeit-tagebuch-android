package com.achtsamkeit.tagebuch.domain.usecase

import com.achtsamkeit.tagebuch.domain.model.GuidedQuestion
import com.achtsamkeit.tagebuch.domain.repository.JournalRepository
import javax.inject.Inject

class GetRandomQuestionUseCase @Inject constructor(
    private val repository: JournalRepository
) {
    suspend operator fun invoke(): GuidedQuestion? = repository.getRandomQuestion()
}

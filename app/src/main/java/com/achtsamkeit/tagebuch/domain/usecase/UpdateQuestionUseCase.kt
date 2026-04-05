package com.achtsamkeit.tagebuch.domain.usecase

import com.achtsamkeit.tagebuch.domain.model.GuidedQuestion
import com.achtsamkeit.tagebuch.domain.repository.JournalRepository
import javax.inject.Inject

class UpdateQuestionUseCase @Inject constructor(
    private val repository: JournalRepository
) {
    suspend operator fun invoke(question: GuidedQuestion) {
        // Since JournalRepository only has insertQuestion, we assume it's an upsert
        // or we should check if there's an update method.
        // Looking at JournalRepository, it has insertQuestion.
        repository.insertQuestion(question)
    }
}

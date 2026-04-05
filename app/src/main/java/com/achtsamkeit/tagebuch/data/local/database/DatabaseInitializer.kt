package com.achtsamkeit.tagebuch.data.local.database

import com.achtsamkeit.tagebuch.domain.model.GuidedQuestion
import com.achtsamkeit.tagebuch.domain.repository.JournalRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseInitializer @Inject constructor(
    private val repository: JournalRepository
) {
    suspend fun initializeIfNeeded() {
        val existingQuestions = repository.getAllQuestions().first()
        if (existingQuestions.isEmpty()) {
            val defaultQuestions = listOf(
                GuidedQuestion(question = "Wofür bist du heute besonders dankbar?", category = "Dankbarkeit"),
                GuidedQuestion(question = "Was war der schönste Moment des Tages?", category = "Reflexion"),
                GuidedQuestion(question = "Was hast du heute über dich selbst gelernt?", category = "Selbstkenntnis"),
                GuidedQuestion(question = "Welche Herausforderung hast du heute gemeistert?", category = "Resilienz"),
                GuidedQuestion(question = "Was hättest du heute gerne anders gemacht?", category = "Wachstum"),
                GuidedQuestion(question = "Wer hat deinen Tag heute bereichert?", category = "Beziehungen"),
                GuidedQuestion(question = "Welchen kleinen Erfolg konntest du heute feiern?", category = "Erfolg"),
                GuidedQuestion(question = "Wie hast du heute für dich selbst gesorgt?", category = "Selbstfürsorge")
            )
            
            defaultQuestions.forEach { question ->
                repository.insertQuestion(question)
            }
        }
    }
}

package com.achtsamkeit.tagebuch.domain.repository

import com.achtsamkeit.tagebuch.domain.model.GuidedQuestion
import com.achtsamkeit.tagebuch.domain.model.JournalEntry
import kotlinx.coroutines.flow.Flow

interface JournalRepository {
    fun getAllEntries(): Flow<List<JournalEntry>>
    suspend fun getEntryById(id: Long): JournalEntry?
    suspend fun insertEntry(entry: JournalEntry): Long
    suspend fun updateEntry(entry: JournalEntry)
    suspend fun deleteEntry(entry: JournalEntry)

    fun getAllQuestions(): Flow<List<GuidedQuestion>>
    fun getSelectedQuestions(): Flow<List<GuidedQuestion>>
    suspend fun insertQuestion(question: GuidedQuestion)
    suspend fun getRandomQuestion(): GuidedQuestion?
}

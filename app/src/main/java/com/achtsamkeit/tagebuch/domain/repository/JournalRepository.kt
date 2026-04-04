package com.achtsamkeit.tagebuch.domain.repository

import com.achtsamkeit.tagebuch.data.local.entities.GuidedQuestionEntity
import com.achtsamkeit.tagebuch.data.local.entities.JournalEntryEntity
import kotlinx.coroutines.flow.Flow

interface JournalRepository {
    fun getAllEntries(): Flow<List<JournalEntryEntity>>
    suspend fun getEntryById(id: Long): JournalEntryEntity?
    suspend fun insertEntry(entry: JournalEntryEntity): Long
    suspend fun updateEntry(entry: JournalEntryEntity)
    suspend fun deleteEntry(entry: JournalEntryEntity)

    // Geführte Fragen
    fun getAllQuestions(): Flow<List<GuidedQuestionEntity>>
    suspend fun insertQuestion(question: GuidedQuestionEntity)
    suspend fun getRandomQuestion(): GuidedQuestionEntity?
}

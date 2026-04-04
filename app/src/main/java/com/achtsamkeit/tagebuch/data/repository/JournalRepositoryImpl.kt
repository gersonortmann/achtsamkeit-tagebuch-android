package com.achtsamkeit.tagebuch.data.repository

import com.achtsamkeit.tagebuch.data.local.dao.GuidedQuestionDao
import com.achtsamkeit.tagebuch.data.local.dao.JournalEntryDao
import com.achtsamkeit.tagebuch.data.local.entities.GuidedQuestionEntity
import com.achtsamkeit.tagebuch.data.local.entities.JournalEntryEntity
import com.achtsamkeit.tagebuch.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class JournalRepositoryImpl @Inject constructor(
    private val journalDao: JournalEntryDao,
    private val questionDao: GuidedQuestionDao
) : JournalRepository {
    override fun getAllEntries(): Flow<List<JournalEntryEntity>> = journalDao.getAllEntries()

    override suspend fun getEntryById(id: Long): JournalEntryEntity? = journalDao.getEntryById(id)

    override suspend fun insertEntry(entry: JournalEntryEntity): Long = journalDao.insertEntry(entry)

    override suspend fun updateEntry(entry: JournalEntryEntity) = journalDao.updateEntry(entry)

    override suspend fun deleteEntry(entry: JournalEntryEntity) = journalDao.deleteEntry(entry)

    // Geführte Fragen
    override fun getAllQuestions(): Flow<List<GuidedQuestionEntity>> = questionDao.getAllQuestions()

    override suspend fun insertQuestion(question: GuidedQuestionEntity) = questionDao.insertQuestion(question)

    override suspend fun getRandomQuestion(): GuidedQuestionEntity? = questionDao.getRandomQuestion()
}

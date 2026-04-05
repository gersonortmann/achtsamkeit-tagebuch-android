package com.achtsamkeit.tagebuch.data.repository

import com.achtsamkeit.tagebuch.data.local.dao.GuidedQuestionDao
import com.achtsamkeit.tagebuch.data.local.dao.JournalEntryDao
import com.achtsamkeit.tagebuch.data.local.mapper.toDomain
import com.achtsamkeit.tagebuch.data.local.mapper.toEntity
import com.achtsamkeit.tagebuch.domain.model.GuidedQuestion
import com.achtsamkeit.tagebuch.domain.model.JournalEntry
import com.achtsamkeit.tagebuch.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class JournalRepositoryImpl @Inject constructor(
    private val journalDao: JournalEntryDao,
    private val questionDao: GuidedQuestionDao
) : JournalRepository {

    override fun getAllEntries(): Flow<List<JournalEntry>> =
        journalDao.getAllEntries().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getEntryById(id: Long): JournalEntry? =
        journalDao.getEntryById(id)?.toDomain()

    override suspend fun insertEntry(entry: JournalEntry): Long =
        journalDao.insertEntry(entry.toEntity())

    override suspend fun updateEntry(entry: JournalEntry) =
        journalDao.updateEntry(entry.toEntity())

    override suspend fun deleteEntry(entry: JournalEntry) =
        journalDao.deleteEntry(entry.toEntity())

    override fun getAllQuestions(): Flow<List<GuidedQuestion>> =
        questionDao.getAllQuestions().map { entities -> entities.map { it.toDomain() } }

    override fun getSelectedQuestions(): Flow<List<GuidedQuestion>> =
        questionDao.getSelectedQuestions().map { entities -> entities.map { it.toDomain() } }

    override suspend fun insertQuestion(question: GuidedQuestion) =
        questionDao.insertQuestion(question.toEntity())

    override suspend fun getRandomQuestion(): GuidedQuestion? =
        questionDao.getRandomQuestion()?.toDomain()
}

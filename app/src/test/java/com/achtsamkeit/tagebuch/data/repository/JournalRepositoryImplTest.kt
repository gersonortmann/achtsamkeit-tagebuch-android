package com.achtsamkeit.tagebuch.data.repository

import com.achtsamkeit.tagebuch.data.local.dao.GuidedQuestionDao
import com.achtsamkeit.tagebuch.data.local.dao.JournalEntryDao
import com.achtsamkeit.tagebuch.data.local.entities.GuidedQuestionEntity
import com.achtsamkeit.tagebuch.data.local.entities.JournalEntryEntity
import com.achtsamkeit.tagebuch.domain.model.JournalEntry
import com.achtsamkeit.tagebuch.domain.model.MoodLevel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class JournalRepositoryImplTest {

    private lateinit var repository: JournalRepositoryImpl
    private val journalDao: JournalEntryDao = mockk()
    private val questionDao: GuidedQuestionDao = mockk()

    @Before
    fun setup() {
        repository = JournalRepositoryImpl(journalDao, questionDao)
    }

    @Test
    fun `getAllEntries maps entities to domain models`() = runTest {
        val entity = JournalEntryEntity(
            id = 1,
            date = LocalDate.of(2024, 1, 1).toEpochDay(),
            createdAt = 67890,
            moodScore = 4,
            moodEmoji = "🙂",
            freeText = "Test",
            gratitudeItems = "",
            guidedAnswersJson = "[]",
            labels = ""
        )
        every { journalDao.getAllEntries() } returns flowOf(listOf(entity))

        repository.getAllEntries().take(1).collect { entries ->
            assertEquals(1, entries.size)
            assertEquals(1L, entries[0].id)
            assertEquals("Test", entries[0].freeText)
            assertEquals(4, entries[0].moodScore)
        }
    }

    @Test
    fun `getSelectedQuestions maps entities to domain models`() = runTest {
        val questionEntity = GuidedQuestionEntity(id = 1, question = "Frage?", isSelected = true)
        every { questionDao.getSelectedQuestions() } returns flowOf(listOf(questionEntity))

        repository.getSelectedQuestions().take(1).collect { questions ->
            assertEquals(1, questions.size)
            assertEquals("Frage?", questions[0].question)
            assertEquals(true, questions[0].isSelected)
        }
    }

    @Test
    fun `insertEntry converts domain model to entity`() = runTest {
        coEvery { journalDao.insertEntry(any()) } returns 1L

        val entry = JournalEntry(
            date = LocalDate.now(),
            createdAt = LocalDateTime.now(),
            moodScore = MoodLevel.GUT.score,
            moodEmoji = MoodLevel.GUT.emoji,
            freeText = "Guter Tag"
        )
        val result = repository.insertEntry(entry)

        assertEquals(1L, result)
        coVerify { journalDao.insertEntry(any()) }
    }
}

package com.achtsamkeit.tagebuch.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.achtsamkeit.tagebuch.data.local.database.AppDatabase
import com.achtsamkeit.tagebuch.data.local.entities.GuidedQuestionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GuidedQuestionDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: GuidedQuestionDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.guidedQuestionDao
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetAllQuestions() = runBlocking {
        val questions = listOf(
            GuidedQuestionEntity(id = 1, question = "Frage 1", isSelected = true),
            GuidedQuestionEntity(id = 2, question = "Frage 2", isSelected = false)
        )

        questions.forEach { dao.insertQuestion(it) }
        
        val allQuestions = dao.getAllQuestions().first()
        assertEquals(2, allQuestions.size)
    }

    @Test
    fun getSelectedQuestionsOnly() = runBlocking {
        val questions = listOf(
            GuidedQuestionEntity(id = 1, question = "Aktiv", isSelected = true),
            GuidedQuestionEntity(id = 2, question = "Inaktiv", isSelected = false)
        )

        questions.forEach { dao.insertQuestion(it) }

        val selected = dao.getSelectedQuestions().first()
        assertEquals(1, selected.size)
        assertEquals("Aktiv", selected[0].question)
    }
}

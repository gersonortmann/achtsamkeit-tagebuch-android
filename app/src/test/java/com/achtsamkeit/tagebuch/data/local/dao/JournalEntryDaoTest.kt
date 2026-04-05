package com.achtsamkeit.tagebuch.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.achtsamkeit.tagebuch.data.local.database.AppDatabase
import com.achtsamkeit.tagebuch.data.local.entities.JournalEntryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
class JournalEntryDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: JournalEntryDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.journalEntryDao
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetEntry() = runBlocking {
        val now = System.currentTimeMillis()
        val entry = JournalEntryEntity(
            id = 1,
            date = LocalDate.now().toEpochDay(),
            createdAt = now,
            moodScore = 4,
            moodEmoji = "🙂",
            freeText = "Heute war ein guter Tag.",
            gratitudeItems = "Kaffee|Sonne",
            guidedAnswersJson = """[{"question":"Wie war dein Tag?","answer":"Gut"}]"""
        )

        dao.insertEntry(entry)
        val allEntries = dao.getAllEntries().first()

        assertEquals(1, allEntries.size)
        assertEquals("🙂", allEntries[0].moodEmoji)
        assertEquals("Kaffee|Sonne", allEntries[0].gratitudeItems)
        assertEquals("""[{"question":"Wie war dein Tag?","answer":"Gut"}]""", allEntries[0].guidedAnswersJson)
    }

    @Test
    fun getEntryById() = runBlocking {
        val entry = JournalEntryEntity(
            id = 100,
            date = LocalDate.now().toEpochDay(),
            createdAt = System.currentTimeMillis(),
            moodScore = 5,
            moodEmoji = "🤩",
            freeText = "Super!",
            gratitudeItems = "",
            guidedAnswersJson = "[]"
        )
        dao.insertEntry(entry)

        val retrieved = dao.getEntryById(100)
        assertNotNull(retrieved)
        assertEquals("🤩", retrieved?.moodEmoji)
    }

    @Test
    fun deleteEntry() = runBlocking {
        val entry = JournalEntryEntity(
            id = 1,
            date = LocalDate.now().toEpochDay(),
            createdAt = System.currentTimeMillis(),
            moodScore = 3,
            moodEmoji = "😐",
            freeText = "Neutral",
            gratitudeItems = "",
            guidedAnswersJson = "[]"
        )
        dao.insertEntry(entry)
        dao.deleteEntry(entry)

        val allEntries = dao.getAllEntries().first()
        assertEquals(0, allEntries.size)
    }
}

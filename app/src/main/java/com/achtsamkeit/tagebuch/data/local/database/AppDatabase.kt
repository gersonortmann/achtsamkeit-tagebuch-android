package com.achtsamkeit.tagebuch.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.achtsamkeit.tagebuch.core.utils.Constants
import com.achtsamkeit.tagebuch.data.local.dao.GuidedQuestionDao
import com.achtsamkeit.tagebuch.data.local.dao.JournalEntryDao
import com.achtsamkeit.tagebuch.data.local.entities.GuidedQuestionEntity
import com.achtsamkeit.tagebuch.data.local.entities.JournalEntryEntity

@Database(
    entities = [JournalEntryEntity::class, GuidedQuestionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract val journalEntryDao: JournalEntryDao
    abstract val guidedQuestionDao: GuidedQuestionDao
}

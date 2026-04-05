package com.achtsamkeit.tagebuch.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.achtsamkeit.tagebuch.data.local.dao.GuidedQuestionDao
import com.achtsamkeit.tagebuch.data.local.dao.JournalEntryDao
import com.achtsamkeit.tagebuch.data.local.entities.GuidedQuestionEntity
import com.achtsamkeit.tagebuch.data.local.entities.JournalEntryEntity

@Database(
    entities = [JournalEntryEntity::class, GuidedQuestionEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract val journalEntryDao: JournalEntryDao
    abstract val guidedQuestionDao: GuidedQuestionDao

    companion object {
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE journal_entries ADD COLUMN tags TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}

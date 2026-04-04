package com.achtsamkeit.tagebuch.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,           // Zeitstempel
    val content: String,      // Der eigentliche Text
    val mood: String?,        // Optionales Mood-Emoji oder ID
    val isFavorite: Boolean = false,
    val aiSummary: String? = null // Platzhalter für Phase 7 (Gemini)
)

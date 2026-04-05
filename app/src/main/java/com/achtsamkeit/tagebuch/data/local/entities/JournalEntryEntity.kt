package com.achtsamkeit.tagebuch.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,           // Zeitstempel (LocalDate to epoch day)
    val createdAt: Long,      // LocalDateTime to epoch milli
    val moodScore: Int,
    val moodEmoji: String,
    val freeText: String,
    val gratitudeItems: String, // Pipe-getrennte Liste
    val guidedAnswersJson: String, // Speichert Fragen und Antworten als JSON
    val tags: String = "" // Pipe-getrennte Tags
)

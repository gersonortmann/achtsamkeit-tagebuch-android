package com.achtsamkeit.tagebuch.domain.model

data class JournalEntry(
    val id: String,
    val text: String,
    val timestamp: Long
)

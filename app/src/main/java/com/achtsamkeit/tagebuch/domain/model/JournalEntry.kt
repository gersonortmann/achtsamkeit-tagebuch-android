package com.achtsamkeit.tagebuch.domain.model

import java.time.LocalDate
import java.time.LocalDateTime



data class JournalEntry(
    val id: Long = 0,
    val date: LocalDate = LocalDate.now(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val moodScore: Int,
    val moodEmoji: String,
    val freeText: String,
    val gratitudeItems: List<String> = emptyList(),
    val guidedAnswers: List<GuidedAnswer> = emptyList(),
    val labels: List<String> = emptyList()
)

enum class MoodLevel(val score: Int, val emoji: String, val label: String) {
    SEHR_SCHLECHT(1, "😔", "Sehr schlecht"),
    SCHLECHT(2, "😕", "Schlecht"),
    NEUTRAL(3, "😐", "Neutral"),
    GUT(4, "🙂", "Gut"),
    SEHR_GUT(5, "😄", "Sehr gut");

    companion object {
        fun fromScore(score: Int): MoodLevel {
            return entries.find { it.score == score } ?: NEUTRAL
        }
    }
}

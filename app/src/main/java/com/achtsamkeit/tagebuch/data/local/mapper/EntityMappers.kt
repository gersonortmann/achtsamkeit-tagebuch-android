package com.achtsamkeit.tagebuch.data.local.mapper

import com.achtsamkeit.tagebuch.data.local.entities.GuidedQuestionEntity
import com.achtsamkeit.tagebuch.data.local.entities.JournalEntryEntity
import com.achtsamkeit.tagebuch.domain.model.GuidedAnswer
import com.achtsamkeit.tagebuch.domain.model.GuidedQuestion
import com.achtsamkeit.tagebuch.domain.model.JournalEntry
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

fun JournalEntryEntity.toDomain(): JournalEntry = JournalEntry(
    id = id,
    date = LocalDate.ofEpochDay(date),
    createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneOffset.UTC),
    moodScore = moodScore,
    moodEmoji = moodEmoji,
    freeText = freeText,
    gratitudeItems = if (gratitudeItems.isBlank()) emptyList() else gratitudeItems.split("|"),
    guidedAnswers = try {
        Json.decodeFromString(guidedAnswersJson)
    } catch (e: Exception) {
        emptyList()
    },
    labels = if (labels.isBlank()) emptyList() else labels.split("|")
)

fun JournalEntry.toEntity(): JournalEntryEntity = JournalEntryEntity(
    id = id,
    date = date.toEpochDay(),
    createdAt = createdAt.toInstant(ZoneOffset.UTC).toEpochMilli(),
    moodScore = moodScore,
    moodEmoji = moodEmoji,
    freeText = freeText,
    gratitudeItems = gratitudeItems.filter { it.isNotBlank() }.joinToString("|"),
    guidedAnswersJson = Json.encodeToString(guidedAnswers),
    labels = labels.filter { it.isNotBlank() }.joinToString("|")
)

fun GuidedQuestionEntity.toDomain(): GuidedQuestion = GuidedQuestion(
    id = id,
    question = question,
    category = category,
    isDefault = isDefault,
    isSelected = isSelected
)

fun GuidedQuestion.toEntity(): GuidedQuestionEntity = GuidedQuestionEntity(
    id = id,
    question = question,
    category = category,
    isDefault = isDefault,
    isSelected = isSelected
)

package com.achtsamkeit.tagebuch.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guided_questions")
data class GuidedQuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val question: String,
    val category: String? = null,
    val isDefault: Boolean = true,
    val isSelected: Boolean = true
)

package com.achtsamkeit.tagebuch.domain.model

data class GuidedQuestion(
    val id: Long = 0,
    val question: String,
    val category: String? = null,
    val isDefault: Boolean = true,
    val isSelected: Boolean = true
)

package com.achtsamkeit.tagebuch.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class GuidedAnswer(
    val question: String,
    val answer: String
)

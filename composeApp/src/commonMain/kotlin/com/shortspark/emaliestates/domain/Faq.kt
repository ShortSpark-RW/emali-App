package com.shortspark.emaliestates.domain

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Serializable
data class Faq(
    val id: String,
    val question: String,
    val answer: String? = null,
    val subject: String,
    val createdAt: Instant,
    val updatedAt: Instant
)

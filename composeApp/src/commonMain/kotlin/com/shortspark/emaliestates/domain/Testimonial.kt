package com.shortspark.emaliestates.domain

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Serializable
data class Testimonial(
    val id: String,
    val userId: String,
    val occupation: String,
    val content: String,
    val starRating: Int,
    val createdAt: Instant,
    val updatedAt: Instant
)

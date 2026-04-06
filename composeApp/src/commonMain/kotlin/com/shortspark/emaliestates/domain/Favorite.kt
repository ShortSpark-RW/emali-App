package com.shortspark.emaliestates.domain

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Serializable
data class Favorite(
    val id: String,
    val userId: String,
    val propertyId: String,
    val createdAt: Instant,
    val updatedAt: Instant
)

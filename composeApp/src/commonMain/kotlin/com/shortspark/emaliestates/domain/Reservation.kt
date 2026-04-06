package com.shortspark.emaliestates.domain

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Serializable
data class Reservation(
    val id: String,
    val userId: String,
    val propertyId: String,
    val shareId: String? = null,
    val status: String,
    val reservedAt: Instant,
    val expiresAt: Instant,
    val createdAt: Instant
)

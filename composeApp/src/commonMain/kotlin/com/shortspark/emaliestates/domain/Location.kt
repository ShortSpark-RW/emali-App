package com.shortspark.emaliestates.domain

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Serializable
data class Location(
    val id: String,
    val latitude: Float,
    val longitude: Float,
    val address: String? = null,
    val propertyId: String? = null,
    val userId: String? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

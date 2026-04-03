package com.shortspark.emaliestates.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents a user's favorite property.
 * Composite key: (userId, propertyId) should be unique.
 */
@Serializable
data class Favorite(
    val id: String,
    val userId: String,
    val propertyId: String,
    val createdAt: Instant
)

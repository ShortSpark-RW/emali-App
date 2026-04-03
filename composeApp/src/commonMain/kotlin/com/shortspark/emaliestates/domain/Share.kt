package com.shortspark.emaliestates.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents a property share/affiliate link.
 * Used for referral tracking and shared property links.
 */
@Serializable
data class Share(
    val id: String,
    val propertyId: String,
    val senderId: String,
    val recipientId: String? = null,
    val parentShareId: String? = null,
    val status: String = "PENDING", // PENDING, SOLD, BOOKED, REJECTED
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

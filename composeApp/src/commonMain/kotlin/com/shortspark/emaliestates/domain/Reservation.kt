package com.shortspark.emaliestates.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents a property reservation/hold.
 * Has an expiry time and status.
 */
@Serializable
data class Reservation(
    val id: String,
    val userId: String,
    val propertyId: String,
    val shareId: String? = null, // Optional: if reserved via a share link
    val status: ReservationStatus = ReservationStatus.PENDING,
    val reservedAt: Instant,
    val expiresAt: Instant,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

@Serializable
enum class ReservationStatus {
    PENDING,
    CONFIRMED,
    EXPIRED,
    CANCELLED
}

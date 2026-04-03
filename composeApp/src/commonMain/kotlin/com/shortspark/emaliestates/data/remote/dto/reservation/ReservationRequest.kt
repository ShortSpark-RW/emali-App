package com.shortspark.emaliestates.data.remote.dto.reservation

import kotlinx.serialization.Serializable

/**
 * Request DTO for creating a reservation.
 * Matches POST /reservations endpoint.
 */
@Serializable
data class ReservationRequest(
    val userId: String,
    val propertyId: String,
    val shareId: String? = null,
    val status: String = "pending", // pending, confirmed, expired, cancelled
    val reservedAt: String, // ISO8601
    val expiresAt: String // ISO8601
)

/**
 * Response DTO for reservation.
 */
@Serializable
data class ReservationResponse(
    val id: String,
    val userId: String,
    val propertyId: String,
    val shareId: String? = null,
    val status: String,
    val reservedAt: String,
    val expiresAt: String,
    val createdAt: String,
    val updatedAt: String
)

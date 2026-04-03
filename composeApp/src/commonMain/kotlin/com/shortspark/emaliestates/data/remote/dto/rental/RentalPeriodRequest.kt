package com.shortspark.emaliestates.data.remote.dto.rental

import kotlinx.serialization.Serializable

/**
 * Request DTO for creating/updating a rental period.
 * Matches POST /rental-periods and PUT /rental-periods/:id endpoints.
 */
@Serializable
data class RentalPeriodRequest(
    val propertyId: String,
    val periodType: String, // DAILY, WEEKLY, MONTHLY
    val price: Double,
    val minNights: Int? = null,
    val maxNights: Int? = null,
    val availableFrom: String? = null, // ISO8601
    val availableTo: String? = null, // ISO8601
    val isActive: Boolean = true
)

/**
 * Response DTO for rental period.
 */
@Serializable
data class RentalPeriodResponse(
    val id: String,
    val propertyId: String,
    val periodType: String,
    val price: Double,
    val minNights: Int? = null,
    val maxNights: Int? = null,
    val availableFrom: String? = null,
    val availableTo: String? = null,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)

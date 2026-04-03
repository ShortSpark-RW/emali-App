package com.shortspark.emaliestates.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents a rental period pricing for a property.
 * Used for rental rates with different period types.
 */
@Serializable
data class RentalPeriod(
    val id: String,
    val propertyId: String,
    val periodType: RentalType,
    val price: Double,
    val minNights: Int? = null,
    val maxNights: Int? = null,
    val availableFrom: Instant? = null,
    val availableTo: Instant? = null,
    val isActive: Boolean = true,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

@Serializable
enum class RentalType {
    DAILY,
    MONTHLY,
    YEARLY
}

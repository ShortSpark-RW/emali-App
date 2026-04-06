package com.shortspark.emaliestates.domain

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class RentalPeriod(
    val id: String,
    val period: RentalType,
    val price: Float,
    val propertyId: String
)

@Serializable
enum class RentalType {
    DAILY,
    MONTHLY,
    YEARLY
}

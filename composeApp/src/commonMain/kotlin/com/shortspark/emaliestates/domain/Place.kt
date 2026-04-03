package com.shortspark.emaliestates.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents a Place (city, suburb, neighborhood).
 * Used for geographic grouping of properties.
 * May include denormalized administrative names for convenience.
 */
@Serializable
data class Place(
    val id: String,
    val name: String,
    val province: String? = null,
    val district: String? = null,
    val sector: String? = null,
    val cell: String? = null,
    val village: String? = null,
    val postalCode: String? = null,
    val image: String? = null, // Image URL
    val order: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

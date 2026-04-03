package com.shortspark.emaliestates.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents a property category (e.g., Apartment, House, Land, Commercial).
 * Used for categorizing and filtering properties.
 */
@Serializable
data class Category(
    val id: String,
    val name: String,
    val description: String? = null,
    val icon: String? = null, // Icon name or URL
    val color: String? = null, // Hex color code
    val order: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

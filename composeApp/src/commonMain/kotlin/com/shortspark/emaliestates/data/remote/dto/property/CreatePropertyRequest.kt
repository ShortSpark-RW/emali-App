package com.shortspark.emaliestates.data.remote.dto.property

import kotlinx.serialization.Serializable

/**
 * Request DTO for creating a property.
 * Matches POST /properties endpoint.
 */
@Serializable
data class CreatePropertyRequest(
    val title: String,
    val description: String,
    val type: String, // Use PropertyType enum value
    val saleType: String, // Use SaleType enum value
    val price: Double,
    val location: LocationDto,
    val isActive: Boolean = true,
    val isFurnished: Boolean = false,
    val furnishingType: String? = null, // Use FurnishingType enum value
    val bedrooms: Int = 0,
    val bathrooms: Int = 0,
    val area: Double,
    val amenities: List<String> = emptyList(),
    val utilities: List<String> = emptyList(),
    val videoUrl: String? = null,
    val floorPlanUrl: String? = null,
    val categoryId: String? = null,
    val placeId: String? = null
)

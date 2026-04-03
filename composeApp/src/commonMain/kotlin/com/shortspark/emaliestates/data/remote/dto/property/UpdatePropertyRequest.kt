package com.shortspark.emaliestates.data.remote.dto.property

import kotlinx.serialization.Serializable

/**
 * Request DTO for updating a property.
 * Matches PUT /properties/:id endpoint.
 * All fields are optional (partial update).
 */
@Serializable
data class UpdatePropertyRequest(
    val title: String? = null,
    val description: String? = null,
    val type: String? = null,
    val saleType: String? = null,
    val price: Double? = null,
    val location: LocationDto? = null,
    val isActive: Boolean? = null,
    val isFurnished: Boolean? = null,
    val furnishingType: String? = null,
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val area: Double? = null,
    val amenities: List<String>? = null,
    val utilities: List<String>? = null,
    val videoUrl: String? = null,
    val floorPlanUrl: String? = null,
    val categoryId: String? = null,
    val placeId: String? = null
)

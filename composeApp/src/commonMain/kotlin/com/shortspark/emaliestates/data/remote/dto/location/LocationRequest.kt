package com.shortspark.emaliestates.data.remote.dto.location

import kotlinx.serialization.Serializable

/**
 * Request DTO for creating/updating a location.
 * Matches POST /locations and PUT /locations/:id endpoints.
 */
@Serializable
data class LocationRequest(
    val provinceId: String? = null,
    val districtId: String? = null,
    val sectorId: String? = null,
    val cellId: String? = null,
    val villageId: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null
)

/**
 * Response DTO for location.
 */
@Serializable
data class LocationResponse(
    val id: String,
    val provinceId: String? = null,
    val districtId: String? = null,
    val sectorId: String? = null,
    val cellId: String? = null,
    val villageId: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null,
    val createdAt: String,
    val updatedAt: String
)

package com.shortspark.emaliestates.data.remote.dto.property

import kotlinx.serialization.Serializable

/**
 * Location DTO for property requests.
 * Matches the Location object in the API.
 */
@Serializable
data class LocationDto(
    val province: String? = null,
    val district: String? = null,
    val sector: String? = null,
    val cell: String? = null,
    val village: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null
)

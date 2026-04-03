package com.shortspark.emaliestates.data.remote.dto.location

import kotlinx.serialization.Serializable

/**
 * Request DTO for creating a sector.
 * Matches POST /sectors endpoint.
 */
@Serializable
data class SectorRequest(
    val name: String,
    val districtId: String,
    val code: String? = null
)

/**
 * Response DTO for sector.
 */
@Serializable
data class SectorResponse(
    val id: String,
    val name: String,
    val districtId: String,
    val code: String? = null,
    val createdAt: String,
    val updatedAt: String
)

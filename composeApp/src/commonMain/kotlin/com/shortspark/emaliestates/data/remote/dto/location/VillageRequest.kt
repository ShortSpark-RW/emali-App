package com.shortspark.emaliestates.data.remote.dto.location

import kotlinx.serialization.Serializable

/**
 * Request DTO for creating a village.
 * Matches POST /villages endpoint.
 */
@Serializable
data class VillageRequest(
    val name: String,
    val cellId: String,
    val code: String? = null
)

/**
 * Response DTO for village.
 */
@Serializable
data class VillageResponse(
    val id: String,
    val name: String,
    val cellId: String,
    val code: String? = null,
    val createdAt: String,
    val updatedAt: String
)

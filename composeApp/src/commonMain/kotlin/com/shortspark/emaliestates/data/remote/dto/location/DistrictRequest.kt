package com.shortspark.emaliestates.data.remote.dto.location

import kotlinx.serialization.Serializable

/**
 * Request DTO for creating a district.
 * Matches POST /districts endpoint.
 */
@Serializable
data class DistrictRequest(
    val name: String,
    val provinceId: String,
    val code: String? = null
)

/**
 * Response DTO for district.
 */
@Serializable
data class DistrictResponse(
    val id: String,
    val name: String,
    val provinceId: String,
    val code: String? = null,
    val createdAt: String,
    val updatedAt: String
)

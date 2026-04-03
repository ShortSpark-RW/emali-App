package com.shortspark.emaliestates.data.remote.dto.location

import kotlinx.serialization.Serializable

/**
 * Request DTO for creating a province.
 * Matches POST /provinces endpoint.
 */
@Serializable
data class ProvinceRequest(
    val name: String,
    val code: String? = null
)

/**
 * Response DTO for province.
 */
@Serializable
data class ProvinceResponse(
    val id: String,
    val name: String,
    val code: String? = null,
    val createdAt: String,
    val updatedAt: String
)

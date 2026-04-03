package com.shortspark.emaliestates.data.remote.dto.favorite

import kotlinx.serialization.Serializable

/**
 * Request DTO for adding a favorite.
 * Matches POST /favorites endpoint.
 */
@Serializable
data class FavoriteRequest(
    val userId: String,
    val propertyId: String
)

/**
 * Response DTO for favorite.
 */
@Serializable
data class FavoriteResponse(
    val id: String,
    val userId: String,
    val propertyId: String,
    val createdAt: String
)

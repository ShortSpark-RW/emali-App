package com.shortspark.emaliestates.data.remote.dto.place

import kotlinx.serialization.Serializable

/**
 * Request DTO for creating/updating a place.
 * Matches POST /places and PUT /places/:id endpoints.
 */
@Serializable
data class PlaceRequest(
    val name: String,
    val province: String? = null,
    val district: String? = null,
    val sector: String? = null,
    val cell: String? = null,
    val village: String? = null,
    val postalCode: String? = null,
    val order: Int = 0,
    val isActive: Boolean = true
)

/**
 * Response DTO for place.
 */
@Serializable
data class PlaceResponse(
    val id: String,
    val name: String,
    val province: String? = null,
    val district: String? = null,
    val sector: String? = null,
    val cell: String? = null,
    val village: String? = null,
    val postalCode: String? = null,
    val image: String? = null,
    val order: Int,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)

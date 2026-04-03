package com.shortspark.emaliestates.data.remote.dto.location

import kotlinx.serialization.Serializable

/**
 * Request DTO for creating a cell.
 * Matches POST /cells endpoint.
 */
@Serializable
data class CellRequest(
    val name: String,
    val sectorId: String,
    val code: String? = null
)

/**
 * Response DTO for cell.
 */
@Serializable
data class CellResponse(
    val id: String,
    val name: String,
    val sectorId: String,
    val code: String? = null,
    val createdAt: String,
    val updatedAt: String
)

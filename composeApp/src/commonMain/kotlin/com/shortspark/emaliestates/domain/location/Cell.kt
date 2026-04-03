package com.shortspark.emaliestates.domain.location

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents a Cell (Akagari) in Rwanda's administrative hierarchy.
 * Fourth level: belongs to a Sector.
 */
@Serializable
data class Cell(
    val id: String,
    val name: String,
    val sectorId: String,
    val code: String? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

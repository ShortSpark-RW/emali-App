package com.shortspark.emaliestates.domain.location

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents a Sector (Umurenge) in Rwanda's administrative hierarchy.
 * Third level: belongs to a District.
 */
@Serializable
data class Sector(
    val id: String,
    val name: String,
    val districtId: String,
    val code: String? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

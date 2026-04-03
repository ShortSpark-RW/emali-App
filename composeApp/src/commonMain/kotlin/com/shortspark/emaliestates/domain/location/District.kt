package com.shortspark.emaliestates.domain.location

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents a District (Akarere) in Rwanda's administrative hierarchy.
 * Second level: belongs to a Province.
 */
@Serializable
data class District(
    val id: String,
    val name: String,
    val provinceId: String,
    val code: String? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

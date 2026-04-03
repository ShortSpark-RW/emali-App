package com.shortspark.emaliestates.domain.location

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents a Village (Umudugudu) in Rwanda's administrative hierarchy.
 * Fifth and final level: belongs to a Cell.
 */
@Serializable
data class Village(
    val id: String,
    val name: String,
    val cellId: String,
    val code: String? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

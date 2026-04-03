package com.shortspark.emaliestates.domain.location

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents a Province (Intara) in Rwanda's administrative hierarchy.
 * Top-level administrative division.
 */
@Serializable
data class Province(
    val id: String,
    val name: String,
    val code: String? = null, // Optional: e.g., "KGL" for Kigali
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

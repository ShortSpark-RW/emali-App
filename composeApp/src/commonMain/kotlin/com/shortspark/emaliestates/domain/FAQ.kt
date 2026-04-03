package com.shortspark.emaliestates.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents a Frequently Asked Question (FAQ).
 * Used for help/FAQ sections.
 */
@Serializable
data class FAQ(
    val id: String,
    val question: String,
    val answer: String,
    val order: Int = 0, // For ordering display
    val category: String? = null, // e.g., "Account", "Properties", "Payments"
    val isActive: Boolean = true,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

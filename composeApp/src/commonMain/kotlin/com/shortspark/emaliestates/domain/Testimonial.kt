package com.shortspark.emaliestates.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents a user testimonial/review for a property.
 * Rating is on a scale of 1-5.
 */
@Serializable
data class Testimonial(
    val id: String,
    val userId: String,
    val propertyId: String,
    val rating: Int, // 1-5
    val comment: String? = null,
    val title: String? = null,
    val isApproved: Boolean = false,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

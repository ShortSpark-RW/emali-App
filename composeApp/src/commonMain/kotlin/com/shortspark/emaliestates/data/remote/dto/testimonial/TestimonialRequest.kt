package com.shortspark.emaliestates.data.remote.dto.testimonial

import kotlinx.serialization.Serializable

/**
 * Request DTO for creating a testimonial.
 * Matches POST /testimonials endpoint.
 */
@Serializable
data class TestimonialRequest(
    val userId: String,
    val propertyId: String,
    val rating: Int, // 1-5
    val comment: String? = null,
    val title: String? = null
)

/**
 * Request DTO for updating a testimonial (partial update).
 */
@Serializable
data class UpdateTestimonialRequest(
    val rating: Int? = null,
    val comment: String? = null,
    val title: String? = null
)

/**
 * Response DTO for testimonial.
 */
@Serializable
data class TestimonialResponse(
    val id: String,
    val userId: String,
    val propertyId: String,
    val rating: Int,
    val comment: String? = null,
    val title: String? = null,
    val isApproved: Boolean,
    val createdAt: String,
    val updatedAt: String
)

package com.shortspark.emaliestates.data.remote.dto.faq

import kotlinx.serialization.Serializable

/**
 * Request DTO for creating/updating a FAQ.
 * Matches POST /faqs and PUT /faqs/:id endpoints.
 */
@Serializable
data class FAQRequest(
    val question: String,
    val answer: String,
    val order: Int = 0,
    val category: String? = null,
    val isActive: Boolean = true
)

/**
 * Response DTO for FAQ.
 */
@Serializable
data class FAQResponse(
    val id: String,
    val question: String,
    val answer: String,
    val order: Int,
    val category: String? = null,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)

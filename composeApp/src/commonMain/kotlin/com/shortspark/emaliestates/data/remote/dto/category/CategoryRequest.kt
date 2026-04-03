package com.shortspark.emaliestates.data.remote.dto.category

import kotlinx.serialization.Serializable

/**
 * Request DTO for creating/updating a category.
 * Matches POST /categories and PUT /categories/:id endpoints.
 */
@Serializable
data class CategoryRequest(
    val name: String,
    val description: String? = null,
    val icon: String? = null,
    val color: String? = null,
    val order: Int = 0,
    val isActive: Boolean = true
)

/**
 * Response DTO for category.
 */
@Serializable
data class CategoryResponse(
    val id: String,
    val name: String,
    val description: String? = null,
    val icon: String? = null,
    val color: String? = null,
    val order: Int,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)

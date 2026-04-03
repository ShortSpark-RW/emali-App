package com.shortspark.emaliestates.domain

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String,
    val name: String,
    val description: String? = null,
    val iconUrl: String? = null,
    val imageUrl: String? = null,
    val parentId: String? = null,
    val sortOrder: Int = 0,
    val isActive: Boolean = true,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

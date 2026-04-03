package com.shortspark.emaliestates.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents a blog article/post.
 * Can be associated with a property, category, and author.
 */
@Serializable
data class Blog(
    val id: String,
    val title: String? = null,
    val content: String? = null,
    val authorId: String,
    val categoryId: String? = null,
    val propertyId: String? = null,
    val featuredImg: String? = null,
    val slug: String? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

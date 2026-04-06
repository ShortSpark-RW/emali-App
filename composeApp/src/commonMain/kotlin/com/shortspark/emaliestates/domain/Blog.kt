package com.shortspark.emaliestates.domain

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
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
    val createdAt: Instant,
    val updatedAt: Instant
)

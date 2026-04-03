package com.shortspark.emaliestates.domain

import kotlinx.serialization.Serializable

@Serializable
data class Meta(
    val total: Int,
    val page: Int? = null,
    val limit: Int? = null,
    val totalPages: Int? = null,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean
)
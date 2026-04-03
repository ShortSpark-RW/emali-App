package com.shortspark.emaliestates.domain

import kotlinx.serialization.Serializable

@Serializable
data class ApiPaginatedResponse<T>(
    val status: String,
    val message: String,
    val data: T,
    val meta: PaginationMeta
)

@Serializable
data class PaginationMeta(
    val total: Int,
    val page: Int,
    val limit: Int,
    val totalPages: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean
)
package com.shortspark.emaliestates.domain

import kotlinx.serialization.Serializable

// Generic wrapper for list responses with metadata
@Serializable
data class ListData<T>(
    val items: List<T>,
    val count: Int? = null
)

// Example: Properties list response
typealias PropertiesResponse = ApiPaginatedResponse<List<Property>>
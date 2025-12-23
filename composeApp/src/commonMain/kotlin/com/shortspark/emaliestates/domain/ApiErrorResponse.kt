package com.shortspark.emaliestates.domain

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponse(
    val status: String,
    val error: String? = null,
    val message: String,
    val statusCode: Int? = null
)
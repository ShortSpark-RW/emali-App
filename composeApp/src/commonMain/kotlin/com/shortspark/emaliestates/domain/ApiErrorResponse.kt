package com.shortspark.emaliestates.domain

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponse(
    val error: String,
    val message: String,
    val statusCode: Int
)
package com.shortspark.emaliestates.domain

import kotlinx.serialization.Serializable

@Serializable
data class ApiSuccessResponse<T>(
    val message: String,
    val data: T,
    val meta: Meta? = null
)
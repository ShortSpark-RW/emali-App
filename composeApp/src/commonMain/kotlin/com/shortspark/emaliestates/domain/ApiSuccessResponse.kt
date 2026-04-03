package com.shortspark.emaliestates.domain

import com.shortspark.emaliestates.util.helpers.MessageSerializer
import kotlinx.serialization.Serializable

@Serializable
data class ApiSuccessResponse<T>(
    val status: String? = null,
    @Serializable(with = MessageSerializer::class)
    val message: List<String>,
    val data: T? = null
)
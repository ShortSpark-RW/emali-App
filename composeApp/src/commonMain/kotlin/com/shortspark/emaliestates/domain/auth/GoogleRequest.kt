package com.shortspark.emaliestates.domain.auth

import kotlinx.serialization.Serializable

@Serializable
data class GoogleRequest (
    val access_token: String? = null
)
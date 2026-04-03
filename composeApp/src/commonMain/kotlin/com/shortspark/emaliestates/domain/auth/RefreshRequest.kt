package com.shortspark.emaliestates.domain.auth

import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequest(
    val refresh_token: String
)

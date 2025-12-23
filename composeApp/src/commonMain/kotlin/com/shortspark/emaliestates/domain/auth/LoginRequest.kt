package com.shortspark.emaliestates.domain.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val identifier: String,
    val password: String
)

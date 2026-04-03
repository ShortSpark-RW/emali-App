package com.shortspark.emaliestates.domain

import com.shortspark.emaliestates.domain.auth.User
import kotlinx.serialization.Serializable

@Serializable
data class AuthSession(
    val accessToken: String,
    val refreshToken: String?,
    val user: User
)
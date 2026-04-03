package com.shortspark.emaliestates.domain.auth

import com.shortspark.emaliestates.domain.ApiSuccessResponse
import kotlinx.serialization.Serializable

@Serializable
data class AuthData(
    val accessToken: String,
    val refreshToken: String? = null,
    val user: User
)

typealias AuthResponse = ApiSuccessResponse<AuthData>

package com.shortspark.emaliestates.domain.auth

import com.shortspark.emaliestates.domain.ApiSuccessResponse
import kotlinx.serialization.Serializable

@Serializable
data class AuthData(
    val accessToken: String,
    val refreshToken: String? = null,
    val user: User
)

@Serializable
data class User(
    val id: String,
    val email: String,
    val phone: String? = null,
    val username: String? = null,
    val name: String,
    val role: String,
    val isActive: Boolean? = false,
    val isVerified: Boolean? = false,
)

typealias AuthResponse = ApiSuccessResponse<AuthData>

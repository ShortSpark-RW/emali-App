package com.shortspark.emaliestates.data.remote.dto.auth

import kotlinx.serialization.Serializable

/**
 * Request DTO for forgot password.
 * Matches POST /auth/forgot-password endpoint.
 */
@Serializable
data class ForgotPasswordRequest(
    val email: String
)

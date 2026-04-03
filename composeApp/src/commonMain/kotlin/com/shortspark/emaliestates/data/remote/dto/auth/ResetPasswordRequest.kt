package com.shortspark.emaliestates.data.remote.dto.auth

import kotlinx.serialization.Serializable

/**
 * Request DTO for reset password.
 * Matches POST /auth/reset-password endpoint.
 */
@Serializable
data class ResetPasswordRequest(
    val email: String,
    val token: String,
    val newPassword: String
)

package com.shortspark.emaliestates.data.remote.dto.auth

import kotlinx.serialization.Serializable

/**
 * Request DTO for OTP verification.
 * Matches POST /auth/verify-otp endpoint.
 */
@Serializable
data class VerifyOtpRequest(
    val otp: String
)

package com.shortspark.emaliestates.domain.auth

import kotlinx.serialization.Serializable

@Serializable
data class ResendOtpRequest(
    val email: String
)

@Serializable
data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

@Serializable
data class UpdateProfileRequest(
    val fullName: String? = null,
    val phone: String? = null,
    val gender: String? = null,
    val dateOfBirth: String? = null, // Format: YYYY-MM-DD
    val locationId: String? = null
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

@Serializable
data class PasswordResetRequest(
    val email: String
)

@Serializable
data class VerifyResetPasswordRequest(
    val email: String,
    val otp: String,
    val newPassword: String
)

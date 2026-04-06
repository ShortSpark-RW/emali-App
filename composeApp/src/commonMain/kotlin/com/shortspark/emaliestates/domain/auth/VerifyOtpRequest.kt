package com.shortspark.emaliestates.domain.auth

import kotlinx.serialization.Serializable

@Serializable
data class VerifyOtpRequest(
    val email: String,
    val otp: String
)

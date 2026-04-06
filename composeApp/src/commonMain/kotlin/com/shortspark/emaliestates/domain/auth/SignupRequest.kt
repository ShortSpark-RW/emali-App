package com.shortspark.emaliestates.domain.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignupRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val phone: String,
    val gender: String,
    val dateOfBirth: String? = null // Format: YYYY-MM-DD
)

package com.shortspark.emaliestates.domain.auth

import kotlinx.serialization.Serializable

/**
 * Request payload for user signup.
 * Combines data from both signup screens.
 */
@Serializable
data class SignupRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val phone: String? = null,
    val gender: String? = null, // "male", "female", "other"
    val dob: String? = null, // YYYY-MM-DD format
    val username: String? = null
)

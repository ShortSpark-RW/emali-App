package com.shortspark.emaliestates.data.remote.dto.auth

import kotlinx.serialization.Serializable

/**
 * Request DTO for user signup.
 * Matches POST /auth/signup endpoint.
 * All fields except email, phone, name, password are optional.
 */
@Serializable
data class SignupRequest(
    val email: String,
    val phone: String,
    val name: String,
    val password: String,
    val role: String? = null, // CLIENT, AGENT, OWNER
    val username: String? = null,
    val gender: String? = null,
    val dob: String? = null, // YYYY-MM-DD
    val address: String? = null,
    val country: String? = null,
    val province: String? = null,
    val district: String? = null,
    val whatsapp: String? = null,
    val profileImg: String? = null,
    val coverImg: String? = null,
    val language: String? = null // EN, FR, SW, RW
)

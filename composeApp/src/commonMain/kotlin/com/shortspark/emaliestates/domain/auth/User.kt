package com.shortspark.emaliestates.domain.auth

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents a user in the system.
 * Aligns with the backend API User model.
 */
@Serializable
data class User(
    val id: String,
    val email: String,
    val phone: String? = null,
    val username: String? = null,
    val name: String,
    val role: String, // CLIENT, AGENT, OWNER

    // Optional profile fields
    val gender: String? = null, // "male", "female", "other"
    val dob: String? = null, // YYYY-MM-DD format
    val address: String? = null,
    val country: String? = null,
    val province: String? = null,
    val district: String? = null,
    val whatsapp: String? = null,
    val profileImg: String? = null, // URL
    val coverImg: String? = null, // URL
    val language: String? = null, // EN, FR, SW, RW

    // Status flags
    val isActive: Boolean? = false,
    val isVerified: Boolean? = false,

    // Timestamps
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

@Serializable
enum class RoleType {
    OWNER,
    AGENT,
    CLIENT
}

@Serializable
enum class Language {
    EN,
    FR,
    SW,
    RW
}

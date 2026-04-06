package com.shortspark.emaliestates.domain

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Serializable
data class User(
    val id: String,
    val email: String,
    val phone: String? = null,
    val name: String,
    val password: String? = null,
    val role: RoleType = RoleType.CLIENT,
    val locationId: String? = null,
    val userId: String? = null,
    val username: String? = null,
    val gender: String? = null,
    val dob: Instant? = null,
    val address: String? = null,
    val country: String? = null,
    val province: String? = null,
    val district: String? = null,
    val profileImg: String? = null,
    val coverImg: String? = null,
    val googleId: String? = null,
    val whatsapp: String? = null,
    val isVerified: Boolean = false,
    val isActive: Boolean = true,
    val lastLogin: Instant? = null,
    val language: Language? = null,
    val createdAt: Instant,
    val updatedAt: Instant
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

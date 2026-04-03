package com.shortspark.emaliestates.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents user preferences/settings.
 * Includes notification and SMS preferences.
 */
@Serializable
data class UserSetting(
    val id: String,
    val userId: String,
    val notificationsOn: Boolean = true,
    val smsEnabled: Boolean = false,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

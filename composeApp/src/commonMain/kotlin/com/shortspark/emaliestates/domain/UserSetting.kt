package com.shortspark.emaliestates.domain

import kotlinx.serialization.Serializable

@Serializable
data class UserSetting(
    val id: String,
    val userId: String,
    val notificationsOn: Boolean = true,
    val smsEnabled: Boolean = true
)

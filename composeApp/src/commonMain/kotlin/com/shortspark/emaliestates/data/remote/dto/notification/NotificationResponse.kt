package com.shortspark.emaliestates.data.remote.dto.notification

import kotlinx.serialization.Serializable

/**
 * Response DTO for notification.
 */
@Serializable
data class NotificationResponse(
    val id: String,
    val userId: String,
    val title: String,
    val body: String,
    val type: String? = null,
    val data: Map<String, String>? = null,
    val isRead: Boolean,
    val createdAt: String
)

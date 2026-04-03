package com.shortspark.emaliestates.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents a user notification.
 * Can be used for in-app notifications or push notification payloads.
 */
@Serializable
data class Notification(
    val id: String,
    val userId: String,
    val title: String,
    val body: String,
    val type: NotificationType? = null,
    val data: Map<String, String>? = null, // Additional payload as key-value pairs
    val isRead: Boolean = false,
    val createdAt: Instant
)

@Serializable
enum class NotificationType {
    SYSTEM,
    PROPERTY,
    RESERVATION,
    PAYMENT,
    MESSAGE,
    ALERT
}

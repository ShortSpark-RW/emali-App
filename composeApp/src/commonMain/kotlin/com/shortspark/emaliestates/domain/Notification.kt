package com.shortspark.emaliestates.domain

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Serializable
data class Notification(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val isRead: Boolean = false,
    val type: String,
    val isSent: Boolean = false,
    val notificationData: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)

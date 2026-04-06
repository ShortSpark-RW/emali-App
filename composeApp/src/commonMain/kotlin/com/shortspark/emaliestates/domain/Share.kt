package com.shortspark.emaliestates.domain

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Serializable
data class Share(
    val id: String,
    val status: String = "PENDING",
    val propertyId: String,
    val senderId: String,
    val recipientId: String? = null,
    val parentShareId: String? = null,
    val createdAt: Instant
)

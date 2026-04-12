package com.shortspark.emaliestates.domain

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Serializable
data class Place(
    val id: String,
    val name: String,
    val featuredImg: String? = null,
    val sectorId: String? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

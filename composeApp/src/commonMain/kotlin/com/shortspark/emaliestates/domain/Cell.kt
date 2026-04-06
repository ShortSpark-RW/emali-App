package com.shortspark.emaliestates.domain

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Serializable
data class Cell(
    val id: String,
    val name: String,
    val sectorId: String,
    val createdAt: Instant,
    val updatedAt: Instant
)

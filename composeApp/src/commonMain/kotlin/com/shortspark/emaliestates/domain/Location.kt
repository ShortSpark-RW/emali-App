package com.shortspark.emaliestates.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents a physical location with geographic coordinates.
 * Links to the administrative hierarchy via optional foreign keys.
 * A property can have one Location.
 */
@Serializable
data class Location(
    val id: String,
    val provinceId: String? = null,
    val districtId: String? = null,
    val sectorId: String? = null,
    val cellId: String? = null,
    val villageId: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

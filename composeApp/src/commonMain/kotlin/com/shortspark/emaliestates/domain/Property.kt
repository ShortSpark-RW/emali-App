package com.shortspark.emaliestates.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Property(
    val id: String,
    val title: String,
    val description: String,
    val type: String,
    val saleType: String,
    val price: Float,

    val locationId: String? = null,
    val isActive: Boolean = true,
    val isFurnished: Boolean = false,
    val furnishingType: String? = null,

    val bedrooms: Int = 0,
    val bathrooms: Int = 0,
    val area: Float,

    val featuredImg: String? = null,
    val additionalImgs: List<String> = emptyList(),
    val videoUrl: String? = null,
    val floorPlanUrl: String? = null,

    val amenities: List<String> = emptyList(),
    val utilities: List<String> = emptyList(),

    val isVerified: Boolean = false,
    val isFeatured: Boolean = false,
    val isSold: Boolean = false,
    val isRented: Boolean = false,
    val isShared: Boolean = false,
    val isReserved: Boolean = false,
    val isArchived: Boolean = false,

    val archivedAt: Instant? = null,
    val archivedReason: String? = null,
    val archivedBy: String? = null,

    val upi: String? = null,
    val ownerId: String,
    val categoryId: String? = null,
    val placeId: String? = null,

    val createdAt: Instant,
    val updatedAt: Instant
)



package com.shortspark.emaliestates.domain

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Serializable
data class Property(
    val id: String,
    val title: String,
    val description: String,
    val type: PropertyType,
    val saleType: SaleType,
    val price: Float,

    val locationId: String? = null,
    val isActive: Boolean = true,
    val isFurnished: Boolean = false,
    val furnishingType: FurnishingType = FurnishingType.UNFURNISHED,

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

    // Nested relationship fields from API
    val place: Place? = null,
    val location: Location? = null,
    val owner: User? = null,
    val category: Category? = null,

    // Denormalized relationship fields (used for local DB and easy access)
    val placeName: String? = null,
    val categoryName: String? = null,
    val ownerName: String? = null,
    val ownerPhone: String? = null,
    val ownerProfileImg: String? = null,

    // Location fields (denormalized from Location table)
    val latitude: Float? = null,
    val longitude: Float? = null,
    val address: String? = null,

    val createdAt: Instant,
    val updatedAt: Instant
)

@Serializable
enum class PropertyType {
    HOUSE,
    LAND
}

@Serializable
enum class SaleType {
    SALE,
    RENT,
    BOTH
}

@Serializable
enum class FurnishingType {
    FULLY_FURNISHED,
    SEMI_FURNISHED,
    UNFURNISHED
}

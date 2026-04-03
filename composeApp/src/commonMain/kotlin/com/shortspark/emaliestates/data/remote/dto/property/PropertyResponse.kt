package com.shortspark.emaliestates.data.remote.dto.property

import com.shortspark.emaliestates.domain.auth.User
import kotlinx.serialization.Serializable

/**
 * Response DTO for a property with included relationships.
 * Matches GET /properties/:id and list endpoints with include parameter.
 */
@Serializable
data class PropertyResponse(
    val id: String,
    val title: String,
    val description: String,
    val type: String,
    val saleType: String,
    val price: Double,
    val location: LocationDto? = null,
    val isActive: Boolean,
    val isFurnished: Boolean,
    val furnishingType: String? = null,
    val bedrooms: Int,
    val bathrooms: Int,
    val area: Double,
    val featuredImg: String? = null,
    val additionalImgs: List<String> = emptyList(),
    val videoUrl: String? = null,
    val floorPlanUrl: String? = null,
    val amenities: List<String> = emptyList(),
    val utilities: List<String> = emptyList(),
    val isVerified: Boolean,
    val isFeatured: Boolean,
    val isSold: Boolean,
    val isRented: Boolean,
    val isShared: Boolean,
    val isReserved: Boolean,
    val isArchived: Boolean,
    val archivedAt: String? = null,
    val archivedReason: String? = null,
    val archivedBy: String? = null,
    val upi: String? = null,
    val owner: User? = null,
    val category: CategoryResponse? = null,
    val place: PlaceResponse? = null,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class CategoryResponse(
    val id: String,
    val name: String,
    val description: String? = null,
    val icon: String? = null,
    val color: String? = null
)

@Serializable
data class PlaceResponse(
    val id: String,
    val name: String,
    val province: String? = null,
    val district: String? = null,
    val sector: String? = null,
    val cell: String? = null,
    val village: String? = null,
    val postalCode: String? = null,
    val image: String? = null
)

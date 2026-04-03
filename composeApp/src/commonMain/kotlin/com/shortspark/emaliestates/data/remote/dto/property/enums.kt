package com.shortspark.emaliestates.data.remote.dto.property

/**
 * Enum representing property types.
 * Aligns with backend enum values.
 */
enum class PropertyType(val value: String) {
    HOUSE("HOUSE"),
    APARTMENT("APARTMENT"),
    LAND("LAND"),
    COMMERCIAL("COMMERCIAL");

    companion object {
        fun fromValue(value: String): PropertyType {
            return entries.find { it.value == value } ?: throw IllegalArgumentException("Unknown PropertyType: $value")
        }
    }
}

/**
 * Enum representing sale/rent type.
 */
enum class SaleType(val value: String) {
    FOR_SALE("SALE"),
    FOR_RENT("RENT");

    companion object {
        fun fromValue(value: String): SaleType {
            return entries.find { it.value == value } ?: throw IllegalArgumentException("Unknown SaleType: $value")
        }
    }
}

/**
 * Enum representing furnishing type.
 */
enum class FurnishingType(val value: String) {
    FULLY("FULLY"),
    PARTIALLY("PARTIALLY"),
    UNFURNISHED("UNFURNISHED");

    companion object {
        fun fromValue(value: String): FurnishingType {
            return entries.find { it.value == value } ?: throw IllegalArgumentException("Unknown FurnishingType: $value")
        }
    }
}

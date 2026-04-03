package com.shortspark.emaliestates.data.remote.dto.property

import kotlinx.serialization.Serializable

/**
 * Response DTO for property image upload.
 * Matches response from POST /properties/:id/images.
 */
@Serializable
data class PropertyImageUploadResponse(
    val featuredImg: String,
    val additionalImgs: List<String>
)

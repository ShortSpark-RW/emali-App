package com.shortspark.emaliestates.data.remote.dto.identification

import kotlinx.serialization.Serializable

/**
 * Request DTO for uploading identification document.
 * Matches POST /identifications endpoint (multipart/form-data).
 */
@Serializable
data class IdentificationRequest(
    val type: String, // NATIONAL_ID, PASSPORT, DRIVING_LICENSE
    val countryOfIssue: String? = null,
    val nationalId: String? = null,
    val passport: String? = null,
    val drivingLicense: String? = null
)

/**
 * Response DTO for identification.
 */
@Serializable
data class IdentificationResponse(
    val userId: String,
    val type: String,
    val countryOfIssue: String? = null,
    val document: String, // URL
    val extractedText: String? = null,
    val verificationStatus: String, // PENDING, APPROVED, REJECTED
    val nationalId: String? = null,
    val passport: String? = null,
    val drivingLicense: String? = null,
    val verified: Boolean,
    val verificationDate: String? = null,
    val faceVerified: Boolean,
    val createdAt: String,
    val updatedAt: String
)

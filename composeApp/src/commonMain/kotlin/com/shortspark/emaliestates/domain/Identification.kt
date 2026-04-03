package com.shortspark.emaliestates.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents a user's identification document.
 * Used for verification (ID, Passport, Driving License).
 */
@Serializable
data class Identification(
    val id: String,
    val userId: String,
    val type: IdentificationType,
    val countryOfIssue: String? = null,
    val documentUrl: String? = null, // URL to stored document image
    val extractedText: String? = null, // Raw OCR text
    val verificationStatus: VerificationStatus = VerificationStatus.PENDING,
    val nationalId: String? = null, // For NATIONAL_ID type
    val passport: String? = null, // For PASSPORT type
    val drivingLicense: String? = null, // For DRIVING_LICENSE type
    val verified: Boolean = false,
    val verificationDate: Instant? = null,
    val faceVerified: Boolean = false,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

@Serializable
enum class IdentificationType {
    NATIONAL_ID,
    PASSPORT,
    DRIVING_LICENSE
}

@Serializable
enum class VerificationStatus {
    PENDING,
    APPROVED,
    REJECTED
}

package com.shortspark.emaliestates.domain

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Serializable
data class Identification(
    val id: String,
    val userId: String,
    val document: String,
    val verified: Boolean = false,
    val type: IdentificationType = IdentificationType.NATIONAL_ID,
    val verificationDate: Instant? = null,
    val verificationStatus: VerificationStatus = VerificationStatus.PENDING,
    val passport: String? = null,
    val drivingLicense: String? = null,
    val nationalId: String? = null,
    val countryOfIssue: String? = null,
    val issueDate: Instant? = null,
    val extractedText: String? = null,
    val faceVerified: Boolean = false,
    val createdAt: Instant,
    val updatedAt: Instant
)

@Serializable
enum class IdentificationType {
    PASSPORT,
    DRIVING_LICENSE,
    NATIONAL_ID
}

@Serializable
enum class VerificationStatus {
    PENDING,
    APPROVED,
    REJECTED
}

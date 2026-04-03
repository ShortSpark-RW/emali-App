package com.shortspark.emaliestates.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Represents a payment transaction.
 * Used for tracking payments for reservations, property purchases, etc.
 */
@Serializable
data class Transaction(
    val id: String,
    val userId: String,
    val propertyId: String,
    val amount: Double,
    val method: PaymentMethod,
    val currency: String = "RWF",
    val status: TransactionStatus = TransactionStatus.PENDING,
    val providerRef: String? = null,
    val idempotencyKey: String? = null,
    val metadata: JsonElement? = null,
    val timestamp: Instant,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

@Serializable
enum class TransactionStatus {
    PENDING,
    SUCCESS,
    FAILED
}

@Serializable
enum class PaymentMethod {
    MOMO,
    VISA,
    MASTERCARD
}

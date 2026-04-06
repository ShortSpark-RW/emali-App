package com.shortspark.emaliestates.domain

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Serializable
data class Transaction(
    val id: String,
    val userId: String,
    val propertyId: String,
    val amount: Float,
    val method: PaymentMethod,
    val currency: String = "RWF",
    val status: TransactionStatus = TransactionStatus.PENDING,
    val providerRef: String? = null,
    val idempotencyKey: String? = null,
    val metadata: String? = null,
    val timestamp: Instant
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

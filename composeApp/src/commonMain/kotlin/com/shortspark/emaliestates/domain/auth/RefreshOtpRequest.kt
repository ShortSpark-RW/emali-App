package com.shortspark.emaliestates.domain.auth

import kotlinx.serialization.Serializable

@Serializable
data class RefreshOtpRequest(
    val email: String
)

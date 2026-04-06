package com.shortspark.emaliestates.data.remote

import com.shortspark.emaliestates.domain.auth.*
import com.shortspark.emaliestates.util.helpers.AppConstants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType


class AuthApi(
    private val client: HttpClient
) {

    suspend fun login(
        email: String,
        password: String
    ): AuthResponse {
        println("Login attempt started")
        println("Email: $email, Password: $password")
        println("API call initiated")
        return client.post("${AppConstants.ALL_PROPERTIES_ENDPOINT}/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(
                    LoginRequest(
                        identifier = email,
                        password = password
                    )
                )
        }.body()
    }

    suspend fun signup(
        email: String,
        password: String,
        fullName: String,
        phone: String,
        gender: String,
        dateOfBirth: String? = null
    ): AuthResponse {
        return client.post("${AppConstants.ALL_PROPERTIES_ENDPOINT}/auth/signup") {
            contentType(ContentType.Application.Json)
            setBody(
                SignupRequest(
                    email = email,
                    password = password,
                    fullName = fullName,
                    phone = phone,
                    gender = gender,
                    dateOfBirth = dateOfBirth
                )
            )
        }.body()
    }

    suspend fun logout() {
        return client.post("${AppConstants.ALL_PROPERTIES_ENDPOINT}/auth/logout") {
            // Explicitly setting empty string to match -d ''
            setBody("")
        }.body()
    }

    suspend fun google(
        access_token: String?
    ): AuthResponse {
        return client.post("${AppConstants.ALL_PROPERTIES_ENDPOINT}/auth/google") {
            contentType(ContentType.Application.Json)
            setBody(
                GoogleRequest(
                    access_token = access_token
                )
            )
        }.body()
    }

    suspend fun verifyOtp(
        email: String,
        otp: String
    ): AuthResponse {
        return client.post("${AppConstants.ALL_PROPERTIES_ENDPOINT}/auth/verify-otp") {
            contentType(ContentType.Application.Json)
            setBody(
                VerifyOtpRequest(
                    email = email,
                    otp = otp
                )
            )
        }.body()
    }

    suspend fun resendOtp(
        email: String
    ): AuthResponse {
        return client.post("${AppConstants.ALL_PROPERTIES_ENDPOINT}/auth/resend-otp") {
            contentType(ContentType.Application.Json)
            setBody(
                ResendOtpRequest(email = email)
            )
        }.body()
    }

    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): AuthResponse {
        return client.post("${AppConstants.ALL_PROPERTIES_ENDPOINT}/auth/change-password") {
            contentType(ContentType.Application.Json)
            setBody(
                ChangePasswordRequest(
                    currentPassword = currentPassword,
                    newPassword = newPassword
                )
            )
        }.body()
    }

    suspend fun updateProfile(
        fullName: String? = null,
        phone: String? = null,
        gender: String? = null,
        dateOfBirth: String? = null,
        locationId: String? = null
    ): AuthResponse {
        return client.post("${AppConstants.ALL_PROPERTIES_ENDPOINT}/auth/update-profile") {
            contentType(ContentType.Application.Json)
            setBody(
                UpdateProfileRequest(
                    fullName = fullName,
                    phone = phone,
                    gender = gender,
                    dateOfBirth = dateOfBirth,
                    locationId = locationId
                )
            )
        }.body()
    }

    suspend fun forgotPassword(
        email: String
    ): AuthResponse {
        return client.post("${AppConstants.ALL_PROPERTIES_ENDPOINT}/auth/forgot-password") {
            contentType(ContentType.Application.Json)
            setBody(
                PasswordResetRequest(email = email)
            )
        }.body()
    }

    suspend fun verifyResetPassword(
        email: String,
        otp: String,
        newPassword: String
    ): AuthResponse {
        return client.post("${AppConstants.ALL_PROPERTIES_ENDPOINT}/auth/verify-reset-password") {
            contentType(ContentType.Application.Json)
            setBody(
                VerifyResetPasswordRequest(
                    email = email,
                    otp = otp,
                    newPassword = newPassword
                )
            )
        }.body()
    }

    suspend fun refreshToken(
        refreshToken: String
    ): AuthResponse {
        return client.post("${AppConstants.ALL_PROPERTIES_ENDPOINT}/auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody(
                RefreshTokenRequest(refreshToken = refreshToken)
            )
        }.body()
    }
}
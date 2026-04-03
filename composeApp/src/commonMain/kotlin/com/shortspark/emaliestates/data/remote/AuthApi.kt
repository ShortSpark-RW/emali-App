package com.shortspark.emaliestates.data.remote

import com.shortspark.emaliestates.domain.auth.AuthResponse
import com.shortspark.emaliestates.domain.auth.ForgotPasswordRequest
import com.shortspark.emaliestates.domain.auth.GoogleRequest
import com.shortspark.emaliestates.domain.auth.LoginRequest
import com.shortspark.emaliestates.domain.auth.RefreshOtpRequest
import com.shortspark.emaliestates.domain.auth.RefreshRequest
import com.shortspark.emaliestates.domain.auth.ResetPasswordRequest
import com.shortspark.emaliestates.domain.auth.SignupRequest
import com.shortspark.emaliestates.domain.auth.VerifyOtpRequest
import com.shortspark.emaliestates.util.helpers.AppConstants
import com.shortspark.emaliestates.util.helpers.logger
import com.shortspark.emaliestates.util.helpers.loggerFor
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType


class AuthApi(
    private val client: HttpClient
) {
    private val logger = loggerFor(this)

    suspend fun login(
        email: String,
        password: String
    ): AuthResponse {
        logger.debug("Login attempt started")
        logger.debug("Attempting login for email: $email")
        return client.post("${AppConstants.ALL_PROPERTIES_ENDPOINT}/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(
                    LoginRequest(
                        identifier = email,
                        password = password
                    )
                )
        }.body<AuthResponse>().also {
            logger.debug("Login API call completed successfully")
        }
    }

//    suspend fun signup(
//        username: String,
//        email: String,
//        password: String
//    ): AuthResponse {
//        return client.post("${AppConstants.ALL_PROPERTIES_ENDPOINT}/auth/signup") {
//            contentType(ContentType.Application.Json)
//            setBody(
//                LoginRequest(
//                    identifier = email,
//                    password = password
//                )
//            )
//        }
//    }

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

    suspend fun refreshToken(
        refreshToken: String
    ): AuthResponse {
        return client.post("${AppConstants.ALL_PROPERTIES_ENDPOINT}/auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody(
                RefreshRequest(
                    refresh_token = refreshToken
                )
            )
        }.body()
    }

    suspend fun signup(
        request: SignupRequest
    ): AuthResponse {
        logger.debug("Signup attempt for email: ${request.email}")
        return client.post("${AppConstants.ALL_PROPERTIES_ENDPOINT}/auth/signup") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<AuthResponse>().also {
            logger.debug("Signup API call completed successfully")
        }
    }

    suspend fun forgotPassword(
        request: ForgotPasswordRequest
    ): AuthResponse {
        logger.debug("Forgot password request for email: ${request.email}")
        return client.post("${AppConstants.ALL_PROPERTIES_ENDPOINT}/auth/forgot-password") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<AuthResponse>().also {
            logger.debug("Forgot password API call completed successfully")
        }
    }

    suspend fun verifyOtp(
        request: VerifyOtpRequest
    ): AuthResponse {
        logger.debug("OTP verification for email: ${request.email}")
        return client.post("${AppConstants.ALL_PROPERTIES_ENDPOINT}/auth/verify-otp") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<AuthResponse>().also {
            logger.debug("OTP verification completed successfully")
        }
    }

    suspend fun resetPassword(
        request: ResetPasswordRequest
    ): AuthResponse {
        logger.debug("Reset password for email: ${request.email}")
        return client.post("${AppConstants.ALL_PROPERTIES_ENDPOINT}/auth/reset-password") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<AuthResponse>().also {
            logger.debug("Password reset completed successfully")
        }
    }

    suspend fun refreshOtp(
        request: RefreshOtpRequest
    ): AuthResponse {
        logger.debug("Refresh OTP for email: ${request.email}")
        return client.post("${AppConstants.ALL_PROPERTIES_ENDPOINT}/auth/refresh-otp") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<AuthResponse>().also {
            logger.debug("OTP refresh completed successfully")
        }
    }
}
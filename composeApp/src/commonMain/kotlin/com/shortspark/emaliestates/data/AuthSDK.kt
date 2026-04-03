package com.shortspark.emaliestates.data

import com.shortspark.emaliestates.data.remote.AuthApi
import com.shortspark.emaliestates.data.repository.AuthRepository
import com.shortspark.emaliestates.domain.RequestState
import com.shortspark.emaliestates.domain.auth.AuthData
import com.shortspark.emaliestates.domain.auth.ForgotPasswordRequest
import com.shortspark.emaliestates.domain.auth.RefreshOtpRequest
import com.shortspark.emaliestates.domain.auth.ResetPasswordRequest
import com.shortspark.emaliestates.domain.auth.SignupRequest
import com.shortspark.emaliestates.domain.auth.User
import com.shortspark.emaliestates.domain.auth.VerifyOtpRequest
import com.shortspark.emaliestates.util.helpers.logger
import com.shortspark.emaliestates.util.helpers.loggerFor

class AuthSDK(
    private val api: AuthApi,
    private val authRepository: AuthRepository
) {
    private val logger = loggerFor(this)

    suspend fun login(
        email: String,
        password: String
    ): RequestState<User> {
        logger.debug("Attempting login for email: $email")
        return try {
            val response = api.login(email, password)

            if (response.status != "success") {
                val errorMsg = response.message.joinToString(separator = "\n")
                logger.warning("Login failed: $errorMsg")
                return RequestState.Error(errorMsg)
            }

            response.data?.let { authData ->
                saveAuthData(authData)
                logger.info("Login successful for user: ${authData.user.id}")
                return RequestState.Success(authData.user)
            }
            logger.error("Login response missing user data")
            return RequestState.Error("Login successful but no user data was returned.")
        } catch (e: Exception) {
            logger.error("Login exception", e)
            return RequestState.Error(e.message ?: "Login failed")
        }
    }

    suspend fun signup(
        request: SignupRequest
    ): RequestState<User> {
        logger.debug("Attempting signup for email: ${request.email}")
        return try {
            val response = api.signup(request)

            if (response.status != "success") {
                val errorMsg = response.message.joinToString(separator = "\n")
                logger.warning("Signup failed: $errorMsg")
                return RequestState.Error(errorMsg)
            }

            response.data?.let { authData ->
                saveAuthData(authData)
                logger.info("Signup successful for user: ${authData.user.id}")
                return RequestState.Success(authData.user)
            }
            logger.error("Signup response missing user data")
            return RequestState.Error("Signup successful but no user data was returned.")
        } catch (e: Exception) {
            logger.error("Signup exception", e)
            return RequestState.Error(e.message ?: "Signup failed")
        }
    }

    suspend fun forgotPassword(
        email: String
    ): RequestState<Unit> {
        logger.debug("Attempting forgot password for email: $email")
        return try {
            val response = api.forgotPassword(ForgotPasswordRequest(email))

            if (response.status != "success") {
                val errorMsg = response.message.joinToString(separator = "\n")
                logger.warning("Forgot password failed: $errorMsg")
                return RequestState.Error(errorMsg)
            }

            logger.info("Forgot password OTP sent to: $email")
            return RequestState.Success(Unit)
        } catch (e: Exception) {
            logger.error("Forgot password exception", e)
            return RequestState.Error(e.message ?: "Failed to send OTP")
        }
    }

    suspend fun verifyOtp(
        email: String,
        otp: String
    ): RequestState<Unit> {
        logger.debug("Attempting OTP verification for email: $email")
        return try {
            val response = api.verifyOtp(VerifyOtpRequest(email, otp))

            if (response.status != "success") {
                val errorMsg = response.message.joinToString(separator = "\n")
                logger.warning("OTP verification failed: $errorMsg")
                return RequestState.Error(errorMsg)
            }

            logger.info("OTP verified successfully for email: $email")
            return RequestState.Success(Unit)
        } catch (e: Exception) {
            logger.error("OTP verification exception", e)
            return RequestState.Error(e.message ?: "OTP verification failed")
        }
    }

    suspend fun resetPassword(
        email: String,
        newPassword: String
    ): RequestState<Unit> {
        logger.debug("Attempting reset password for email: $email")
        return try {
            val response = api.resetPassword(ResetPasswordRequest(email, newPassword))

            if (response.status != "success") {
                val errorMsg = response.message.joinToString(separator = "\n")
                logger.warning("Reset password failed: $errorMsg")
                return RequestState.Error(errorMsg)
            }

            logger.info("Password reset successfully for email: $email")
            return RequestState.Success(Unit)
        } catch (e: Exception) {
            logger.error("Reset password exception", e)
            return RequestState.Error(e.message ?: "Password reset failed")
        }
    }

    suspend fun refreshOtp(
        email: String
    ): RequestState<Unit> {
        logger.debug("Attempting refresh OTP for email: $email")
        return try {
            val response = api.refreshOtp(RefreshOtpRequest(email))

            if (response.status != "success") {
                val errorMsg = response.message.joinToString(separator = "\n")
                logger.warning("Refresh OTP failed: $errorMsg")
                return RequestState.Error(errorMsg)
            }

            logger.info("OTP refreshed successfully for email: $email")
            return RequestState.Success(Unit)
        } catch (e: Exception) {
            logger.error("Refresh OTP exception", e)
            return RequestState.Error(e.message ?: "Refresh OTP failed")
        }
    }

    suspend fun logout() {
        try {
            // 1. Attempt server-side logout first
            api.logout()
        } catch (e: Exception) {
            // Log error but continue - server may reject expired token
            logger.warning("Logout API call failed or token expired: ${e.message}", e)
        } finally {
            // 2. ALWAYS clear local session
            authRepository.clearSession()
            logger.info("Local session cleared")
        }
    }

    suspend fun google(access_token: String?): RequestState<User> {
        return try {
            val response = api.google(access_token)

            if (response.status != "success") {
                return RequestState.Error(response.message.joinToString(separator = "\n"))
            }

            response.data?.let { authData ->
                saveAuthData(authData)
                return RequestState.Success(authData.user)
            }
            return RequestState.Error("Login successful but no user data was returned.")
        } catch (e: Exception) {
            return RequestState.Error(e.message ?: "Google login failed")
        }
    }

    /**
     * Refresh access token using refresh token.
     * Returns a new access token or null if refresh fails.
     */
    suspend fun refreshToken(): RequestState<Unit> {
        return try {
            val refreshToken = authRepository.getRefreshToken()
            if (refreshToken.isNullOrBlank()) {
                return RequestState.Error("No refresh token available. Please log in again.")
            }

            val response = api.refreshToken(refreshToken)

            if (response.status != "success") {
                return RequestState.Error(response.message.joinToString(separator = "\n"))
            }

            response.data?.let { authData ->
                authRepository.saveToken(
                    accessToken = authData.accessToken,
                    refreshToken = authData.refreshToken,
                )
                logger.info("Access token refreshed successfully")
                return RequestState.Success(Unit)
            }
            return RequestState.Error("Token refresh failed: no data returned")
        } catch (e: Exception) {
            logger.error("Token refresh exception", e)
            return RequestState.Error(e.message ?: "Token refresh failed")
        }
    }

    private fun saveAuthData(authData: com.shortspark.emaliestates.domain.auth.AuthData) {
        authRepository.saveToken(
            accessToken = authData.accessToken,
            refreshToken = authData.refreshToken,
        )
        // Save user to local database
        authRepository.saveUser(authData.user)
    }

    /**
     * Get the currently authenticated user from local storage.
     */
    fun getCurrentUser(): User? {
        return authRepository.getCurrentUser()
    }
}

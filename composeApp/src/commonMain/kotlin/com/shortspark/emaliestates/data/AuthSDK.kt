package com.shortspark.emaliestates.data

import com.shortspark.emaliestates.data.remote.AuthApi
import com.shortspark.emaliestates.data.repository.AuthRepository
import com.shortspark.emaliestates.domain.RequestState
import com.shortspark.emaliestates.domain.auth.User

class AuthSDK(
    private val api: AuthApi,
    private val authRepository: AuthRepository
) {

    suspend fun login(
        email: String,
        password: String
    ): RequestState<Any> {
        return try {
            val response = api.login(email, password)

            if (response.status != "success") {
                return RequestState.Error(response.message.joinToString(separator = "\n"))
            }

            response.data?.let { authData ->
                authRepository.saveToken(
                    accessToken = authData.accessToken,
                    refreshToken = authData.refreshToken,
                )
                return RequestState.Success(authData.user)
            }
            return RequestState.Error("Login successful but no user data was returned.")
        } catch (e: Exception) {
            return RequestState.Error(e.message ?: "Login failed")
        }
    }

    suspend fun logout() {
        return try {
            // 1. Attempt server-side logout
            // If this returns 401 (Unauthorized), Ktor might throw an exception
            // depending on your validation config, but we catch it below.
            authRepository.clearSession()
        } catch (e: Exception) {
            // 2. Log error but proceed
            println("Logout API call failed or token expired: ${e.message}")
        } finally {
            // 3. FORCE LOCAL WIPE
            // This ensures the user is logged out locally no matter what.
            api.logout()
        }
    }

    suspend fun google(access_token: String?): Any {
        return try {
            val response = api.google(access_token)

            if (response.status != "success") {
                return RequestState.Error(response.message.joinToString(separator = "\n"))
            }

            response.data?.let { authData ->
                authRepository.saveToken(
                    accessToken = authData.accessToken,
                    refreshToken = authData.refreshToken,
                )
                return RequestState.Success(authData.user)
            }
            return RequestState.Error("Login successful but no user data was returned.")

        } catch (e: Exception) {
            return RequestState.Error(e.message ?: "Login failed")
        }
    }

    suspend fun signup(
        email: String,
        password: String,
        fullName: String,
        phone: String,
        gender: String,
        dateOfBirth: String? = null
    ): RequestState<Any> {
        return try {
            val response = api.signup(
                email = email,
                password = password,
                fullName = fullName,
                phone = phone,
                gender = gender,
                dateOfBirth = dateOfBirth
            )

            if (response.status != "success") {
                return RequestState.Error(response.message.joinToString(separator = "\n"))
            }

            // Do NOT save tokens on signup; OTP verification will handle that
            return RequestState.Success(Unit)
        } catch (e: Exception) {
            return RequestState.Error(e.message ?: "Registration failed")
        }
    }

    suspend fun verifyOtp(
        email: String,
        otp: String
    ): RequestState<Any> {
        return try {
            val response = api.verifyOtp(email, otp)

            if (response.status != "success") {
                return RequestState.Error(response.message.joinToString(separator = "\n"))
            }

            response.data?.let { authData ->
                authRepository.saveToken(
                    accessToken = authData.accessToken,
                    refreshToken = authData.refreshToken,
                )
                return RequestState.Success(authData.user)
            }
            return RequestState.Error("Verification successful but no user data was returned.")
        } catch (e: Exception) {
            return RequestState.Error(e.message ?: "Verification failed")
        }
    }

    suspend fun resendOtp(email: String): RequestState<Unit> {
        return try {
            val response = api.resendOtp(email)
            if (response.status != "success") {
                RequestState.Error(response.message.joinToString(separator = "\n"))
            } else {
                RequestState.Success(Unit)
            }
        } catch (e: Exception) {
            RequestState.Error(e.message ?: "Failed to resend verification code")
        }
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): RequestState<Unit> {
        return try {
            val response = api.changePassword(currentPassword, newPassword)
            if (response.status != "success") {
                RequestState.Error(response.message.joinToString(separator = "\n"))
            } else {
                // If password was changed successfully, re-login to refresh token
                RequestState.Success(Unit)
            }
        } catch (e: Exception) {
            RequestState.Error(e.message ?: "Password change failed")
        }
    }

    suspend fun updateProfile(
        fullName: String? = null,
        phone: String? = null,
        gender: String? = null,
        dateOfBirth: String? = null,
        locationId: String? = null
    ): RequestState<Any> {
        return try {
            val response = api.updateProfile(fullName, phone, gender, dateOfBirth, locationId)
            if (response.status != "success") {
                RequestState.Error(response.message.joinToString(separator = "\n"))
            } else {
                response.data?.let { user ->
                    RequestState.Success(user)
                } ?: RequestState.Error("Profile update succeeded but no data returned")
            }
        } catch (e: Exception) {
            RequestState.Error(e.message ?: "Profile update failed")
        }
    }

    suspend fun forgotPassword(email: String): RequestState<Unit> {
        return try {
            val response = api.forgotPassword(email)
            if (response.status != "success") {
                RequestState.Error(response.message.joinToString(separator = "\n"))
            } else {
                RequestState.Success(Unit)
            }
        } catch (e: Exception) {
            RequestState.Error(e.message ?: "Password reset request failed")
        }
    }

    suspend fun verifyResetPassword(email: String, otp: String, newPassword: String): RequestState<Unit> {
        return try {
            val response = api.verifyResetPassword(email, otp, newPassword)
            if (response.status != "success") {
                RequestState.Error(response.message.joinToString(separator = "\n"))
            } else {
                RequestState.Success(Unit)
            }
        } catch (e: Exception) {
            RequestState.Error(e.message ?: "Password reset verification failed")
        }
    }

    suspend fun refreshToken(): RequestState<Unit> {
        return try {
            val tokenInfo = authRepository.getToken()
            val refreshToken = tokenInfo?.refreshToken
            if (refreshToken.isNullOrEmpty()) {
                return RequestState.Error("No refresh token available. Please login again.")
            }

            val response = api.refreshToken(refreshToken)
            if (response.status != "success") {
                // Token refresh failed, clear session
                authRepository.clearSession()
                return RequestState.Error(response.message.joinToString(separator = "\n"))
            }

            response.data?.let { authData ->
                authRepository.saveToken(
                    accessToken = authData.accessToken,
                    refreshToken = authData.refreshToken ?: refreshToken
                )
                return RequestState.Success(Unit)
            }
            return RequestState.Error("Token refresh succeeded but no data returned")
        } catch (e: Exception) {
            authRepository.clearSession()
            return RequestState.Error(e.message ?: "Token refresh failed")
        }
    }

    /**
     * Get the current access token
     */
    suspend fun getAccessToken(): String? {
        return try {
            authRepository.getToken()?.accessToken
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get current user profile
     * Note: This method needs to be implemented based on backend API.
     * For now, it returns null as the backend may not have a /me endpoint yet.
     */
    suspend fun getCurrentUser(): RequestState<User> {
        return try {
            val token = authRepository.getToken()
            if (token == null) {
                return RequestState.Error("No authentication token found")
            }

            // TODO: Call backend API to get current user
            // For now, we need to implement an endpoint like GET /auth/me
            // Response should be AuthResponse with user data
            RequestState.Error("getCurrentUser() not implemented - needs backend endpoint")
        } catch (e: Exception) {
            RequestState.Error(e.message ?: "Failed to get current user")
        }
    }
}

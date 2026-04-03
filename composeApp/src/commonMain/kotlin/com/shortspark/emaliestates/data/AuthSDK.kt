package com.shortspark.emaliestates.data

import com.shortspark.emaliestates.data.remote.AuthApi
import com.shortspark.emaliestates.data.repository.AuthRepository
import com.shortspark.emaliestates.domain.RequestState

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
}

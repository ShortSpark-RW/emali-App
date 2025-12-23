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

    fun logout() {
        authRepository.clearSession()
    }
}

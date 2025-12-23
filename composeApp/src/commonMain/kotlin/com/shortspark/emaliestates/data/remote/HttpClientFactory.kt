package com.shortspark.emaliestates.data.remote

import com.shortspark.emaliestates.data.repository.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class HttpClientFactory(
    private val authRepository: AuthRepository
) {

    fun create(): HttpClient {
        return HttpClient {

            /* ---------- Serialization ---------- */
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        explicitNulls = false
                    }
                )
            }

            /* ---------- Logging ---------- */
            install(Logging) {
                level = LogLevel.ALL
            }

            /* ---------- Authentication ---------- */
            install(Auth) {
                bearer {

                    /**
                     * Step 3 connection point
                     * -----------------------
                     * - Reads tokens from local storage (SQLDelight)
                     * - Attaches Authorization header automatically
                     * - No refresh logic here by design
                     */
                    loadTokens {
                        authRepository.getToken()?.let {
                            BearerTokens(
                                accessToken = it.accessToken,
                                refreshToken = it.refreshToken ?: ""
                            )
                        }
                    }
                }
            }
        }
    }
}

package com.shortspark.emaliestates.data.repository

import com.shortspark.emaliestates.data.local.LocalDatabase

data class TokenInfo(
    val accessToken: String,
    val refreshToken: String?
)

class AuthRepository(
    private val localDatabase: LocalDatabase
) {
    private val queries = localDatabase.authTokenQueries

    fun getToken(): TokenInfo? {
        return queries.getToken()
            .executeAsOneOrNull()
            ?.let { TokenInfo(it.accessToken, it.refreshToken) }
    }

    fun saveToken(
        accessToken: String,
        refreshToken: String?
    ) {
        queries.insertToken(
            accessToken = accessToken,
            refreshToken = refreshToken ?: "",
            expiresAt = ""
        )
    }

    fun clearSession() {
        queries.clearToken()
    }
}
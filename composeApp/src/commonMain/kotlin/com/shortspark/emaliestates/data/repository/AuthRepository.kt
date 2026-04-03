package com.shortspark.emaliestates.data.repository

import com.shortspark.emaliestates.data.local.LocalDatabase
import com.shortspark.emaliestates.domain.auth.User
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class AuthRepository(
    private val localDatabase: LocalDatabase
) {
    private val queries = localDatabase.authTokenQueries
    private val userQueries = localDatabase.userQueries

    /**
     * Retrieve stored authentication token.
     * Note: Currently not checking expiration because backend doesn't provide expiresAt.
     * Refresh token logic handles session renewal.
     */
    fun getToken(): TokenInfo? {
        return queries.getToken()
            .executeAsOneOrNull()
            ?.let { TokenInfo(it.accessToken, it.refreshToken, it.userId) }
    }

    fun getRefreshToken(): String? {
        return queries.getToken()
            .executeAsOneOrNull()
            ?.refreshToken
            ?.takeIf { it.isNotBlank() }
    }

    fun getUserId(): String? {
        return queries.getToken()
            .executeAsOneOrNull()
            ?.userId
            ?.takeIf { it.isNotBlank() }
    }

    fun saveToken(
        accessToken: String,
        refreshToken: String?,
        expiresAt: String? = null,
        userId: String? = null
    ) {
        if (accessToken.isBlank()) return

        queries.insertToken(
            accessToken = accessToken,
            refreshToken = refreshToken ?: "",
            expiresAt = expiresAt,
            userId = userId ?: ""
        )
    }

    fun clearSession() {
        queries.clearToken()
    }

    // User management
    fun saveUser(user: User) {
        // Upsert: insert or replace
        val existing = getUserById(user.id)
        if (existing != null) {
            userQueries.updateUser(
                email = user.email,
                phone = user.phone,
                username = user.username,
                name = user.name,
                role = user.role,
                gender = user.gender,
                dob = user.dob,
                address = user.address,
                country = user.country,
                province = user.province,
                district = user.district,
                whatsapp = user.whatsapp,
                profileImg = user.profileImg,
                coverImg = user.coverImg,
                language = user.language,
                isActive = if (user.isActive == true) 1L else 0L,
                isVerified = if (user.isVerified == true) 1L else 0L,
                updatedAt = user.updatedAt?.toString(),
                id = user.id
            )
        } else {
            userQueries.insertUser(
                id = user.id,
                email = user.email,
                phone = user.phone,
                username = user.username,
                name = user.name,
                role = user.role,
                gender = user.gender,
                dob = user.dob,
                address = user.address,
                country = user.country,
                province = user.province,
                district = user.district,
                whatsapp = user.whatsapp,
                profileImg = user.profileImg,
                coverImg = user.coverImg,
                language = user.language,
                isActive = if (user.isActive == true) 1L else 0L,
                isVerified = if (user.isVerified == true) 1L else 0L,
                createdAt = user.createdAt?.toString(),
                updatedAt = user.updatedAt?.toString()
            )
        }
    }

    fun getUserById(id: String): User? {
        return localDatabase.getUserById(id)
    }

    fun getUserByEmail(email: String): User? {
        return localDatabase.getUserByEmail(email)
    }

    fun deleteUser(id: String) {
        userQueries.deleteUser(id)
    }

    fun clearAllUsers() {
        userQueries.clearAllUsers()
    }

    /**
     * Get the currently authenticated user from the stored token.
     * Returns null if no user is logged in.
     */
    fun getCurrentUser(): User? {
        val userId = getUserId() ?: return null
        return getUserById(userId)
    }
}

data class TokenInfo(
    val accessToken: String,
    val refreshToken: String?,
    val userId: String? = null
)

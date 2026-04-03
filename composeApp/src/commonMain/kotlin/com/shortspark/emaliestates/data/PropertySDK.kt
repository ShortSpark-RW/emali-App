package com.shortspark.emaliestates.data

import com.shortspark.emaliestates.data.repository.PropertyRepository
import com.russhwolf.settings.Settings
import com.shortspark.emaliestates.data.remote.PropertyApi
import com.shortspark.emaliestates.domain.ApiErrorResponse
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.domain.RequestState
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.SerializationException
import kotlin.time.Duration.Companion.minutes
import com.shortspark.emaliestates.util.helpers.loggerFor
import kotlin.time.ExperimentalTime
import java.io.IOException

private const val FRESH_DATA_KEY = "freshDataTimestamp"

@OptIn(ExperimentalTime::class) // FIX: Added OptIn annotation here
class PropertySDK(
    private val api: PropertyApi,
    private val repository: PropertyRepository,
    private val settings: Settings,
    private val clock: Clock
) {

    /**
     * Fetches properties with optional pagination and category filter.
     *
     * @param page Page number (1-indexed)
     * @param limit Number of items per page
     * @param categoryId Optional category ID to filter properties
     *
     * Caching behavior:
     * - If categoryId is null: uses existing 5-minute cache (all properties)
     * - If categoryId is provided: bypasses cache, always fetches from network
     *   (server-side filtering ensures accurate results)
     */
    @Throws(Exception::class)
    suspend fun getAllProperties(
        page: Int = 1,
        limit: Int = 10,
        categoryId: String? = null
    ): RequestState<List<Property>> {
        // If category filter is applied, fetch directly from network (no cache)
        if (categoryId != null) {
            return fetchFromNetwork(page, limit, categoryId)
        }

        // No filter: use cached approach
        return try {
            val cachedProperties = repository.getLocalProperties()

            if (cachedProperties.isEmpty()) {
                // Case 1: No Cache -> Fetch from Network
                fetchAndSave()
            } else {
                if (isDataStale()) {
                    // Case 2: Stale Cache -> Try Network, Fallback to Cache
                    try {
                        val apiResponse = api.fetchAllProperties(page, limit, null)
                        apiResponse.data?.let { properties ->
                            saveToCache(properties)
                            RequestState.Success(properties)
                        } ?: RequestState.Success(cachedProperties)
                    } catch (e: Exception) {
                        // Network error -> Return Stale Cache
                        RequestState.Success(cachedProperties)
                    }
                } else {
                    // Case 3: Fresh Cache -> Return Cache
                    RequestState.Success(cachedProperties)
                }
            }
        } catch (e: Exception) {
            val cachedProperties = repository.getLocalProperties()
            if (cachedProperties.isNotEmpty()) {
                RequestState.Success(cachedProperties)
            } else {
                RequestState.Error(e.message ?: "An unknown error occurred.")
            }
        }
    }

    /**
     * Fetches properties directly from network (no caching).
     * Used when category filter is active to ensure fresh filtered results.
     */
    private suspend fun fetchFromNetwork(
        page: Int,
        limit: Int,
        categoryId: String?
    ): RequestState<List<Property>> {
        return try {
            val apiResponse = api.fetchAllProperties(page, limit, categoryId)
            apiResponse.data?.let { properties ->
                RequestState.Success(properties)
            } ?: RequestState.Error(apiResponse.message.joinToString(", ").ifEmpty { "No properties found" })
        } catch (e: Exception) {
            RequestState.Error(e.message ?: "Failed to fetch properties")
        }
    }

    suspend fun fetchPropertyById(id: String): RequestState<Property?> {
        return try {
            val cachedProperty = repository.getPropertyById(id)

            if (cachedProperty == null) {
                // Case 1: No Cache -> Fetch from Network
                val apiResponse = api.fetchPropertyById(id)
                RequestState.Success(apiResponse.data)
            } else {
                // Case 2: Cache Hit -> Return Cache
                RequestState.Success(cachedProperty)
            }
        } catch (e: ClientRequestException) {
            // 4xx errors
            val errorBody = e.response.body<ApiErrorResponse>()
            RequestState.Error(errorBody.error ?: errorBody.message)
        } catch (e: ServerResponseException) {
            // 5xx errors
            val errorBody = e.response.body<ApiErrorResponse>()
            RequestState.Error(errorBody.error ?: errorBody.message)
        } catch (e: RedirectResponseException) {
            // 3xx errors
            RequestState.Error("Unexpected redirect: ${e.response.status.description}")
        } catch (e: SerializationException) {
            // Response body couldn't be parsed
            RequestState.Error("Failed to parse server response.")
        } catch (e: IOException) {
            // No internet / connection dropped
            RequestState.Error("No internet connection. Please check your network.")
        } catch (e: Exception) {
            // Catch-all fallback
            RequestState.Error(e.message ?: "An unknown error occurred.")
        }
    }

    private suspend fun fetchAndSave(): RequestState<List<Property>> {
        return try {
            val apiResponse = api.fetchAllProperties(page = 1, limit = 10, categoryId = null)

            when {
                apiResponse.data != null -> {
                    saveToCache(apiResponse.data)
                    RequestState.Success(apiResponse.data)
                }
                apiResponse.message.isNotEmpty() -> {
                    RequestState.Error(apiResponse.message.joinToString(", "))
                }
                else -> {
                    RequestState.Error("No data available")
                }
            }
        } catch (e: ClientRequestException) {
            // 4xx errors
            val errorBody = e.response.body<ApiErrorResponse>()
            RequestState.Error(errorBody.error ?: errorBody.message)
        } catch (e: ServerResponseException) {
            // 5xx errors
            val errorBody = e.response.body<ApiErrorResponse>()
            RequestState.Error(errorBody.error ?: errorBody.message)
        } catch (e: RedirectResponseException) {
            // 3xx errors
            RequestState.Error("Unexpected redirect: ${e.response.status.description}")
        } catch (e: SerializationException) {
            // Response body couldn't be parsed
            RequestState.Error("Failed to parse server response.")
        } catch (e: IOException) {
            // No internet / connection dropped
            RequestState.Error("No internet connection. Please check your network.")
        } catch (e: Exception) {
            // Catch-all fallback
            RequestState.Error(e.message ?: "An unknown error occurred.")
        }
    }

    private suspend fun saveToCache(properties: List<Property>) {
        repository.saveProperties(properties)
        settings.putLong(
            FRESH_DATA_KEY,
            clock.now().toEpochMilliseconds()
        )
    }

    private fun isDataStale(): Boolean {
        val savedTimestamp = Instant.fromEpochMilliseconds(
            settings.getLong(FRESH_DATA_KEY, 0L)
        )
        val currentTimestamp = clock.now()

        // Calculate difference safely
        val difference = if (currentTimestamp > savedTimestamp) {
            currentTimestamp - savedTimestamp
        } else {
            savedTimestamp - currentTimestamp
        }

        return difference >= 5.minutes
    }

    suspend fun getCachedProperties(): List<Property> {
        return repository.getLocalProperties()
    }
}

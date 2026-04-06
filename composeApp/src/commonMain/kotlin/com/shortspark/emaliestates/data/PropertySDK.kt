package com.shortspark.emaliestates.data

import PropertyRepository
import com.russhwolf.settings.Settings
import com.shortspark.emaliestates.data.remote.PropertyApi
import com.shortspark.emaliestates.domain.ApiErrorResponse
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.domain.RequestState
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

private const val FRESH_DATA_KEY = "freshDataTimestamp"

@OptIn(ExperimentalTime::class) // FIX: Added OptIn annotation here
class PropertySDK(
    private val api: PropertyApi,
    private val repository: PropertyRepository,
    private val settings: Settings,
    private val clock: Clock
) {

    @Throws(Exception::class)
    suspend fun getAllProperties(): RequestState<List<Property>> {
        return try {
            val cachedProperties = repository.getLocalProperties()

            if (cachedProperties.isEmpty()) {
                // Case 1: No Cache -> Fetch from Network
                fetchAndSave()
            } else {
                if (isDataStale()) {
                    // Case 2: Stale Cache -> Try Network, Fallback to Cache
                    try {
                        val apiResponse = api.fetchAllProperties()
                        apiResponse.data?.let { properties ->
                            saveToCache(properties)
                            RequestState.Success(properties)
                        } ?: RequestState.Success(cachedProperties) // Keep old data if server returns null
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
            // General Fallback
            val cachedProperties = repository.getLocalProperties()
            if (cachedProperties.isNotEmpty()) {
                RequestState.Success(cachedProperties)
            } else {
                RequestState.Error(e.message ?: "An unknown error occurred.")
            }
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
            val apiResponse = api.fetchAllProperties()

            when {
                apiResponse.data != null -> {
                    saveToCache(apiResponse.data)
                    RequestState.Success(apiResponse.data)
                }
                apiResponse.message != null -> {
                    RequestState.Error(apiResponse.message.joinToString(", "))
                }
                else -> {
                    RequestState.Error(apiResponse.message)
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

    /**
     * Search properties with various filters
     */
    suspend fun searchProperties(
        query: String? = null,
        propertyType: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        minSurface: Double? = null,
        maxSurface: Double? = null,
        rooms: Int? = null,
        bathrooms: Int? = null,
        saleType: String? = null
    ): RequestState<List<Property>> {
        return try {
            val apiResponse = api.searchProperties(
                query = query,
                propertyType = propertyType,
                minPrice = minPrice,
                maxPrice = maxPrice,
                minSurface = minSurface,
                maxSurface = maxSurface,
                rooms = rooms,
                bathrooms = bathrooms,
                saleType = saleType
            )
            RequestState.Success(apiResponse.data ?: emptyList())
        } catch (e: ClientRequestException) {
            val errorBody = e.response.body<ApiErrorResponse>()
            RequestState.Error(errorBody.error ?: errorBody.message)
        } catch (e: ServerResponseException) {
            val errorBody = e.response.body<ApiErrorResponse>()
            RequestState.Error(errorBody.error ?: errorBody.message)
        } catch (e: RedirectResponseException) {
            RequestState.Error("Unexpected redirect: ${e.response.status.description}")
        } catch (e: SerializationException) {
            RequestState.Error("Failed to parse server response.")
        } catch (e: IOException) {
            RequestState.Error("No internet connection. Please check your network.")
        } catch (e: Exception) {
            RequestState.Error(e.message ?: "An unknown error occurred.")
        }
    }
}

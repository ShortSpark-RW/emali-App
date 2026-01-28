package com.shortspark.emaliestates.data

import com.russhwolf.settings.Settings
import com.shortspark.emaliestates.data.remote.PropertyApi
import com.shortspark.emaliestates.data.repository.PropertyRepository
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.domain.RequestState
import kotlinx.datetime.Clock
import  kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.hours
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

    private suspend fun fetchAndSave(): RequestState<List<Property>> {
        val apiResponse = api.fetchAllProperties()
        return apiResponse.data?.let { properties ->
            saveToCache(properties)
            RequestState.Success(properties)
        } ?: RequestState.Error("Failed to fetch properties from the network.")
    }

    private fun saveToCache(properties: List<Property>) {
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

        return difference >= 24.hours
    }
}

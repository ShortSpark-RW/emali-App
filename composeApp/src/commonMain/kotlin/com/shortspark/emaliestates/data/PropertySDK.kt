package com.shortspark.emaliestates.data

import com.russhwolf.settings.Settings
import com.shortspark.emaliestates.data.local.LocalDatabase
import com.shortspark.emaliestates.data.remote.PropertyApi
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.domain.RequestState
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

const val FRESH_DATA_KEY = "freshDataTimestamp"

class PropertySDK (
    private val api: PropertyApi,
    private val database: LocalDatabase,
    private val settings: Settings
) {

    @OptIn(ExperimentalTime::class)
    @Throws(Exception::class)
    suspend fun getAllProperties(): RequestState<List<Property>> {
        return try {
            val cachedProperties = database.readAllProperties()
            if (cachedProperties.isEmpty()) {
                settings.putLong(
                    FRESH_DATA_KEY,
                    Clock.System.now().toEpochMilliseconds()
                )
                val apiResponse = api.fetchAllProperties()
                // Safely handle nullable data
                apiResponse.data?.let { properties ->
                    database.removeAllProperties()
                    database.insertAllProperties(properties)
                    RequestState.Success(properties)
                } ?: RequestState.Error("Failed to fetch properties from the network.")
            } else {
                if (isDataStale()) {
                    val apiResponse = api.fetchAllProperties()
                    // Safely handle nullable data here as well
                    apiResponse.data?.let { properties ->
                        database.removeAllProperties()
                        database.insertAllProperties(properties)
                        RequestState.Success(properties)
                    } ?: RequestState.Success(cachedProperties) // Fallback to stale cache if network fails
                } else {
                    RequestState.Success(cachedProperties)
                }
            }
        } catch (e: Exception) {
            // Check for cached data as a fallback on exception
            val cachedProperties = database.readAllProperties()
            if (cachedProperties.isNotEmpty()) {
                RequestState.Success(cachedProperties)
            } else {
                RequestState.Error(e.message ?: "An unknown error occurred.")
            }
        }
    }


    @OptIn(ExperimentalTime::class)
    private fun isDataStale(): Boolean {
        val savedTimestamp = Instant.fromEpochMilliseconds(
            settings.getLong(FRESH_DATA_KEY, 0L)
        )
        val currentTimestamp = Clock.System.now()
        val difference =
            if (savedTimestamp > currentTimestamp) savedTimestamp - currentTimestamp
            else currentTimestamp - savedTimestamp

        return difference >= 24.hours
    }
}
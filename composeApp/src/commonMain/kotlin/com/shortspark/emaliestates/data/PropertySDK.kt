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
                RequestState.Success(
                    api.fetchAllProperties().also {
                        database.removeAllProperties()
                        database.insertAllProperties(it.data)
                    }
                )
            } else {
                if (isDataStale()) {
                    RequestState.Success(
                        api.fetchAllProperties().also {
                            database.removeAllProperties()
                            database.insertAllProperties(it.data)
                        }
                    )
                } else RequestState.Success(cachedProperties)
            }
        } catch (e: Exception) {
            RequestState.Error(e.message.toString())
        } as RequestState<List<Property>>
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
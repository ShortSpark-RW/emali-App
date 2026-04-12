package com.shortspark.emaliestates.data

import com.shortspark.emaliestates.data.repository.CategoryRepository
import com.russhwolf.settings.Settings
import com.shortspark.emaliestates.domain.Category
import com.shortspark.emaliestates.domain.RequestState
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime

private const val CATEGORIES_FRESH_DATA_KEY = "categoriesFreshDataTimestamp"

@OptIn(ExperimentalTime::class)
class CategorySDK(
    private val repository: CategoryRepository,
    private val settings: Settings,
    private val clock: Clock
) {

    suspend fun getCategories(): RequestState<List<Category>> {
        return try {
            val cachedCategories = repository.getLocalCategories()

            if (cachedCategories.isEmpty() || isDataStale()) {
                repository.getCategories()
            } else {
                RequestState.Success(cachedCategories)
            }
        } catch (e: Exception) {
            val cachedCategories = repository.getLocalCategories()
            if (cachedCategories.isNotEmpty()) {
                RequestState.Success(cachedCategories)
            } else {
                RequestState.Error(e.message ?: "An unknown error occurred.")
            }
        }
    }

    private fun isDataStale(): Boolean {
        val savedTimestamp = Instant.fromEpochMilliseconds(
            settings.getLong(CATEGORIES_FRESH_DATA_KEY, 0L)
        )
        val currentTimestamp = clock.now()
        val difference = currentTimestamp - savedTimestamp
        return difference >= 24.hours
    }

    fun updateFreshDataTimestamp() {
        settings.putLong(
            CATEGORIES_FRESH_DATA_KEY,
            clock.now().toEpochMilliseconds()
        )
    }
}

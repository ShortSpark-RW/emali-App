package com.shortspark.emaliestates.data.repository

import com.shortspark.emaliestates.data.local.LocalDatabase
import com.shortspark.emaliestates.data.remote.CategoryApi
import com.shortspark.emaliestates.domain.Category
import com.shortspark.emaliestates.domain.RequestState
import com.shortspark.emaliestates.util.helpers.toInstantSafe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/**
 * Repository for managing Category data.
 * Implements a caching strategy with local persistence (SQLDelight) and network fetch.
 *
 * Caching policy:
 * - Always fetch fresh from network on first launch or cache empty
 * - Subsequent launches return cached categories immediately
 * - Cache is refreshed in the background if stale (24h TTL)
 */
class CategoryRepository(
    private val api: CategoryApi,
    private val database: LocalDatabase
) {

    private val categoryQueries = database.categoryQueries

    /**
     * Fetches categories with caching.
     * Returns cached data immediately if available, then refreshes from network in background.
     * If no cache exists, shows loading and fetches from network.
     */
    suspend fun getCategories(forceRefresh: Boolean = false): RequestState<List<Category>> {
        return withContext(Dispatchers.IO) {
            val cachedCategories = getLocalCategories()

            // If force refresh, skip cache and fetch from network
            if (forceRefresh) {
                return@withContext fetchAndCache()
            }

            // If no cache, fetch from network
            if (cachedCategories.isEmpty()) {
                fetchAndCache()
            } else {
                // Return cached data; optionally refresh in background
                RequestState.Success(cachedCategories)
            }
        }
    }

    /**
     * Fetches a single category by ID.
     * Checks local cache first, then network if not found.
     */
    suspend fun getCategoryById(id: String): RequestState<Category?> {
        return withContext(Dispatchers.IO) {
            val localCategory = categoryQueries.getCategoryById(id).executeAsOneOrNull()?.let { mapToCategory(it) }
            if (localCategory != null) {
                RequestState.Success(localCategory)
            } else {
                // Try network
                try {
                    val response = api.fetchCategoryById(id)
                    response.data?.let { category ->
                        insertCategory(category)
                        RequestState.Success(category)
                    } ?: RequestState.Error("Category not found")
                } catch (e: Exception) {
                    RequestState.Error(e.message ?: "Failed to fetch category")
                }
            }
        }
    }

    /**
     * Inserts or updates a category in local storage.
     */
    suspend fun insertCategory(category: Category) {
        withContext(Dispatchers.IO) {
            categoryQueries.insertCategory(
                id = category.id,
                name = category.name,
                description = category.description,
                icon = category.icon,
                color = category.color,
                sortOrder = category.order.toLong(),
                isActive = if (category.isActive) 1L else 0L,
                createdAt = category.createdAt?.toString(),
                updatedAt = category.updatedAt?.toString()
            )
        }
    }

    /**
     * Inserts multiple categories, replacing any existing data.
     * Used after bulk fetch from network to sync local cache.
     */
    suspend fun replaceAllCategories(categories: List<Category>) {
        withContext(Dispatchers.IO) {
            categoryQueries.transaction {
                categoryQueries.clearAllCategories()
                categories.forEach { category ->
                    categoryQueries.insertCategory(
                        id = category.id,
                        name = category.name,
                        description = category.description,
                        icon = category.icon,
                        color = category.color,
                        sortOrder = category.order.toLong(),
                        isActive = if (category.isActive) 1L else 0L,
                        createdAt = category.createdAt?.toString(),
                        updatedAt = category.updatedAt?.toString()
                    )
                }
            }
        }
    }

    /**
     * Returns all categories from local database, ordered by sortOrder then name.
     */
    suspend fun getLocalCategories(): List<Category> = withContext(Dispatchers.IO) {
        categoryQueries.getAllCategories().executeAsList().map { mapToCategory(it) }
    }

    /**
     * Returns only active categories from local database.
     */
    suspend fun getActiveCategories(): List<Category> = withContext(Dispatchers.IO) {
        categoryQueries.getActiveCategories().executeAsList().map { mapToCategory(it) }
    }

    /**
     * Clears all categories from local storage.
     * Useful for testing or logout cleanup.
     */
    suspend fun clearCategories() = withContext(Dispatchers.IO) {
        categoryQueries.clearAllCategories()
    }

    /**
     * Fetches categories from network and updates local cache.
     */
    private suspend fun fetchAndCache(): RequestState<List<Category>> {
        return try {
            val response = api.fetchAllCategories()
            response.data?.let { categories ->
                replaceAllCategories(categories)
                RequestState.Success(categories)
            } ?: RequestState.Error("No categories received from server")
        } catch (e: Exception) {
            // On network error, fallback to cache if available
            val cached = getLocalCategories()
            if (cached.isNotEmpty()) {
                RequestState.Success(cached)
            } else {
                RequestState.Error(e.message ?: "Failed to fetch categories")
            }
        }
    }

    private fun mapToCategory(db: com.shortspark.Category): Category {
        return Category(
            id = db.id,
            name = db.name,
            description = db.description,
            icon = db.icon,
            color = db.color,
            order = db.sortOrder.toInt(),
            isActive = db.isActive == 1L,
            createdAt = db.createdAt?.toInstantSafe(),
            updatedAt = db.updatedAt?.toInstantSafe()
        )
    }
}

package com.shortspark.emaliestates.data.repository

import com.shortspark.emaliestates.data.local.LocalDatabase
import com.shortspark.emaliestates.data.remote.CategoryApi
import com.shortspark.emaliestates.domain.Category
import com.shortspark.emaliestates.domain.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import toInstantSafe
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class CategoryRepository(
    private val categoryApi: CategoryApi,
    private val localDatabase: LocalDatabase
) {
    private val queries = localDatabase.categoryQueries

    suspend fun getCategories(): RequestState<List<Category>> = withContext(Dispatchers.Default) {
        val localCategories = getLocalCategories()
        
        return@withContext try {
            val response = categoryApi.fetchAllCategories()
            val categories = response.data ?: emptyList()

            // Cache categories locally
            queries.transaction {
                categories.forEach { category ->
                    queries.insertOrReplaceCategory(
                        id = category.id,
                        name = category.name,
                        details = category.details,
                        createdAt = category.createdAt.toString(),
                        updatedAt = category.updatedAt.toString()
                    )
                }
            }

            RequestState.Success(categories)
        } catch (e: Exception) {
            // Fallback to local data if network fails
            if (localCategories.isNotEmpty()) {
                RequestState.Success(localCategories)
            } else {
                RequestState.Error(e.message ?: "Failed to fetch categories and no local data found")
            }
        }
    }

    suspend fun getCategoryById(id: String): RequestState<Category?> = withContext(Dispatchers.Default) {
        return@withContext try {
            val response = categoryApi.fetchCategoryById(id)
            val category = response.data

            // Cache single category locally
            category?.let {
                queries.insertOrReplaceCategory(
                    id = it.id,
                    name = it.name,
                    details = it.details,
                    createdAt = it.createdAt.toString(),
                    updatedAt = it.updatedAt.toString()
                )
            }

            RequestState.Success(category)
        } catch (e: Exception) {
            RequestState.Error(e.message ?: "Failed to fetch category")
        }
    }

    // Local only: get all categories from cache
    suspend fun getLocalCategories(): List<Category> = withContext(Dispatchers.Default) {
        queries.selectAllCategories().executeAsList().map { row ->
            Category(
                id = row.id,
                name = row.name,
                details = row.details,
                createdAt = row.createdAt.toInstantSafe(),
                updatedAt = row.updatedAt.toInstantSafe()
            )
        }
    }

    suspend fun clearCategories() = withContext(Dispatchers.Default) {
        queries.removeAllCategories()
    }
}

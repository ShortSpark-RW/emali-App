package com.shortspark.emaliestates.data.repository

import com.shortspark.emaliestates.data.remote.CategoryApi
import com.shortspark.emaliestates.domain.Category
import com.shortspark.emaliestates.domain.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class CategoryRepository(
    private val categoryApi: CategoryApi
) {
    suspend fun getCategories(): RequestState<List<Category>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = categoryApi.fetchAllCategories()
            RequestState.Success(response.data ?: emptyList())
        } catch (e: Exception) {
            RequestState.Error(e.message ?: "Failed to fetch categories")
        }
    }

    suspend fun getCategoryById(id: String): RequestState<Category?> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = categoryApi.fetchCategoryById(id)
            RequestState.Success(response.data)
        } catch (e: Exception) {
            RequestState.Error(e.message ?: "Failed to fetch category")
        }
    }
}

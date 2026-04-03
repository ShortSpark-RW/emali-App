package com.shortspark.emaliestates.data.remote

import ApiException
import com.shortspark.emaliestates.domain.ApiErrorResponse
import com.shortspark.emaliestates.domain.ApiSuccessResponse
import com.shortspark.emaliestates.domain.Category
import com.shortspark.emaliestates.util.helpers.AppConstants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class CategoryApi(
    private val httpClient: HttpClient
) {

    suspend fun fetchAllCategories(): ApiSuccessResponse<List<Category>> {
        println("INFO: Fetching categories...")
        val response = httpClient.get("${AppConstants.ALL_PROPERTIES_ENDPOINT}/categories")
        return when (response.status.value) {
            in 200..299 -> response.body()
            else -> {
                val error = response.body<ApiErrorResponse>()
                throw ApiException(
                    statusCode = response.status.value,
                    errorMessage = error.error ?: error.message,
                    detail = response.status.description
                )
            }
        }
    }

    suspend fun fetchCategoryById(id: String): ApiSuccessResponse<Category> {
        println("INFO: Fetching category by ID...")
        val response = httpClient.get("${AppConstants.ALL_PROPERTIES_ENDPOINT}/categories/$id")
        return when (response.status.value) {
            in 200..299 -> response.body()
            else -> {
                val error = response.body<ApiErrorResponse>()
                throw ApiException(
                    statusCode = response.status.value,
                    errorMessage = error.error ?: error.message,
                    detail = response.status.description
                )
            }
        }
    }
}

package com.shortspark.emaliestates.data.remote

import ApiException
import com.shortspark.emaliestates.domain.ApiErrorResponse
import com.shortspark.emaliestates.domain.ApiSuccessResponse
import com.shortspark.emaliestates.domain.Category
import com.shortspark.emaliestates.util.helpers.AppConstants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

/**
 * API service for Category-related network calls.
 * Endpoints follow RESTful conventions under the properties base endpoint.
 */
class CategoryApi(
    private val httpClient: HttpClient
) {

    /**
     * Fetches all active categories from the backend.
     * Categories are used for filtering properties and organizing listings.
     *
     * GET /api/v1/categories
     *
     * @return ApiSuccessResponse containing list of Category objects
     * @throws ApiException on network errors or non-2xx responses
     */
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

    /**
     * Fetches a single category by ID.
     *
     * GET /api/v1/categories/{id}
     */
    suspend fun fetchCategoryById(id: String): ApiSuccessResponse<Category> {
        println("INFO: Fetching category by ID: $id")
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

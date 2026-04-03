package com.shortspark.emaliestates.data.remote

import ApiException
import com.shortspark.emaliestates.domain.ApiErrorResponse
import com.shortspark.emaliestates.domain.ApiSuccessResponse
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.util.helpers.AppConstants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get


class PropertyApi(
    private val httpClient: HttpClient
) {

    suspend fun fetchAllProperties(
        page: Int = 1,
        limit: Int = 10,
        categoryId: String? = null
    ): ApiSuccessResponse<List<Property>> {
        println("INFO: Fetching properties... page=$page, limit=$limit, categoryId=$categoryId")
        // Build query parameters
        val queryParams = mutableListOf("limit=$limit", "page=$page")
        categoryId?.let {
            queryParams.add("categoryId=$it")
        }
        val queryString = queryParams.joinToString("&")
        val url = "${AppConstants.ALL_PROPERTIES_ENDPOINT}/properties?$queryString"
        val response = httpClient.get(url)
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

    suspend fun fetchPropertyById(id: String): ApiSuccessResponse<Property> {
        println("INFO: Fetching property by ID...")
        val response = httpClient.get("${AppConstants.ALL_PROPERTIES_ENDPOINT}/properties/$id")
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

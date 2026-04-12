package com.shortspark.emaliestates.data.remote

import ApiException
import com.shortspark.emaliestates.domain.ApiErrorResponse
import com.shortspark.emaliestates.domain.ApiSuccessResponse
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.util.helpers.AppConstants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class PropertyApi(
    private val httpClient: HttpClient
) {

    suspend fun fetchAllProperties(): ApiSuccessResponse<List<Property>> {
        println("INFO: Fetching properties...")
        val response = httpClient.get("${AppConstants.ALL_PROPERTIES_ENDPOINT}/properties?limit=100&page=1&include=place,location,owner,category")
        return when (response.status.value) {
            in 200..299 -> response.body<ApiSuccessResponse<List<Property>>>()
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
        val response = httpClient.get("${AppConstants.ALL_PROPERTIES_ENDPOINT}/properties/$id?include=place,location,owner,category")
        return when (response.status.value) {
            in 200..299 -> response.body<ApiSuccessResponse<Property>>()
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

    suspend fun searchProperties(
        query: String? = null,
        propertyType: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        minSurface: Double? = null,
        maxSurface: Double? = null,
        rooms: Int? = null,
        bathrooms: Int? = null,
        saleType: String? = null
    ): ApiSuccessResponse<List<Property>> {
        println("INFO: Searching properties with filters...")
        val response = httpClient.get("${AppConstants.ALL_PROPERTIES_ENDPOINT}/properties/search") {
            url {
                parameter("include", "place,location,owner,category")
                query?.let { parameter("q", it) }
                propertyType?.let { parameter("type", it) }
                minPrice?.let { parameter("min_price", it) }
                maxPrice?.let { parameter("max_price", it) }
                minSurface?.let { parameter("min_surface", it) }
                maxSurface?.let { parameter("max_surface", it) }
                rooms?.let { parameter("rooms", it) }
                bathrooms?.let { parameter("bathrooms", it) }
                saleType?.let { parameter("sale_type", it) }
            }
        }
        return when (response.status.value) {
            in 200..299 -> response.body<ApiSuccessResponse<List<Property>>>()
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

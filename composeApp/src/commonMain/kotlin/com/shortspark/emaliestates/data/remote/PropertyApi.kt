package com.shortspark.emaliestates.data.remote

import com.shortspark.emaliestates.domain.ApiSuccessResponse
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.util.helpers.AppConstants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get


class PropertyApi(
    private val httpClient: HttpClient
) {

    suspend fun fetchAllProperties(): ApiSuccessResponse<List<Property>> {
        println("INFO: Fetching properties...")
        val response = httpClient.get("${AppConstants.ALL_PROPERTIES_ENDPOINT}/properties?limit=10&page=1")
        return response.body()
    }
}

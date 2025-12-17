package com.shortspark.emaliestates.data.remote

import com.shortspark.emaliestates.domain.ApiSuccessResponse
import com.shortspark.emaliestates.domain.Property
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

const val ALL_PROPERTIES_ENDPOINT = "https://emali-3ro5.onrender.com/api/v1/properties?limit=10&page=1"

class PropertyApi {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    suspend fun fetchAllProperties(): ApiSuccessResponse<List<Property>> {
        println("INFO: Fetching properties...")
        val response = httpClient.get(ALL_PROPERTIES_ENDPOINT)
        return response.body()
    }
}
package com.shortspark.emaliestates.data.remote

import com.shortspark.emaliestates.domain.auth.AuthResponse
import com.shortspark.emaliestates.domain.auth.GoogleRequest
import com.shortspark.emaliestates.domain.auth.LoginRequest
import com.shortspark.emaliestates.util.helpers.AppConstants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType


class AuthApi(
    private val client: HttpClient
) {

    suspend fun login(
        email: String,
        password: String
    ): AuthResponse {
        println("Login attempt started")
        println("Email: $email, Password: $password")
        println("API call initiated")
        return client.post("${AppConstants.ALL_PROPERTIES_ENDPOINT}/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(
                    LoginRequest(
                        identifier = email,
                        password = password
                    )
                )
        }.body()
    }

//    suspend fun signup(
//        username: String,
//        email: String,
//        password: String
//    ): AuthResponse {
//        return client.post("${AppConstants.ALL_PROPERTIES_ENDPOINT}/auth/signup") {
//            contentType(ContentType.Application.Json)
//            setBody(
//                LoginRequest(
//                    identifier = email,
//                    password = password
//                )
//            )
//        }
//    }

    suspend fun logout() {
        return client.post("${AppConstants.ALL_PROPERTIES_ENDPOINT}/auth/logout") {
            // Explicitly setting empty string to match -d ''
            setBody("")
        }.body()
    }

    suspend fun google(
        access_token: String?
    ): AuthResponse {
        return client.post("${AppConstants.ALL_PROPERTIES_ENDPOINT}/auth/google") {
            contentType(ContentType.Application.Json)
            setBody(
                GoogleRequest(
                    access_token = access_token
                )
            )
        }.body()
    }
}
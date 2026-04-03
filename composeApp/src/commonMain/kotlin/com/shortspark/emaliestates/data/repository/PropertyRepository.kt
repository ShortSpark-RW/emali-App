package com.shortspark.emaliestates.data.repository

import com.shortspark.emaliestates.data.local.LocalDatabase
import com.shortspark.emaliestates.domain.Property
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class PropertyRepository(
    private val database: LocalDatabase
) {
    suspend fun getLocalProperties(): List<Property> =
        withContext(Dispatchers.IO) {
            database.readAllProperties()
        }

    suspend fun saveProperties(properties: List<Property>) =
        withContext(Dispatchers.IO) {
            database.removeAllProperties()
            database.insertAllProperties(properties)
        }

    suspend fun getPropertyById(id: String): Property? =
        withContext(Dispatchers.IO) {
            database.readPropertyById(id)
        }

    suspend fun clearProperties() =
        withContext(Dispatchers.IO) {
            database.removeAllProperties()
        }
}
package com.shortspark.emaliestates.data.repository

import com.shortspark.emaliestates.data.local.LocalDatabase
import com.shortspark.emaliestates.domain.Property

class PropertyRepository(
    private val database: LocalDatabase
) {
    fun getLocalProperties(): List<Property> {
        return database.readAllProperties()
    }

    fun saveProperties(properties: List<Property>) {
        // Ideally run in a transaction if your DB wrapper supports it
        database.removeAllProperties()
        database.insertAllProperties(properties)
    }

    fun clearProperties() {
        database.removeAllProperties()
    }
}
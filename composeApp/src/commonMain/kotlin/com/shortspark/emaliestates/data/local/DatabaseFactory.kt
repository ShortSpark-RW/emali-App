package com.shortspark.emaliestates.data.local

import app.cash.sqldelight.db.SqlDriver
import com.shortspark.emaliestates.database.PropertyDatabase
import com.shortspark.emaliestates.domain.Property
import kotlinx.serialization.json.Json
import toInstantSafe
import kotlin.time.ExperimentalTime

interface DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

class LocalDatabase(
    databaseDriverFactory: DatabaseDriverFactory
) {
    private val database = PropertyDatabase(
        databaseDriverFactory.createDriver()
    )

    private val query = database.propertyDatabaseQueries
    val authTokenQueries = database.authTokenQueries

    @OptIn(ExperimentalTime::class)
    fun readAllProperties(): List<Property> {
        return query.readAllProperties()
            .executeAsList()
            .map { dbProperty ->
                Property(
                    id = dbProperty.id,
                    title = dbProperty.title,
                    description = dbProperty.description,
                    type = dbProperty.type,
                    saleType = dbProperty.saleType,
                    price = dbProperty.price.toFloat(),
                    locationId = dbProperty.locationId,
                    isActive = dbProperty.isActive == 1L,
                    isFurnished = dbProperty.isFurnished == 1L,
                    furnishingType = dbProperty.furnishingType,
                    bedrooms = dbProperty.bedrooms.toInt(),
                    bathrooms = dbProperty.bathrooms.toInt(),
                    area = dbProperty.area.toFloat(),
                    featuredImg = dbProperty.featuredImg,
                    additionalImgs = dbProperty.additionalImgs?.let { Json.decodeFromString<List<String>>(it) } ?: emptyList(),
                    videoUrl = dbProperty.videoUrl,
                    floorPlanUrl = dbProperty.floorPlanUrl,
                    amenities = dbProperty.amenities?.let { Json.decodeFromString<List<String>>(it) } ?: emptyList(),
                    utilities = dbProperty.utilities?.let { Json.decodeFromString<List<String>>(it) } ?: emptyList(),
                    isVerified = dbProperty.isVerified == 1L,
                    isFeatured = dbProperty.isFeatured == 1L,
                    isSold = dbProperty.isSold == 1L,
                    isRented = dbProperty.isRented == 1L,
                    isShared = dbProperty.isShared == 1L,
                    isReserved = dbProperty.isReserved == 1L,
                    isArchived = dbProperty.isArchived == 1L,
                    archivedAt = dbProperty.archivedAt?.toInstantSafe(),
                    archivedReason = dbProperty.archivedReason,
                    archivedBy = dbProperty.archivedBy,
                    upi = dbProperty.upi,
                    ownerId = dbProperty.ownerId,
                    categoryId = dbProperty.categoryId,
                    placeId = dbProperty.placeId,
                    createdAt = dbProperty.createdAt.toInstantSafe(),
                    updatedAt = dbProperty.updatedAt.toInstantSafe()
                )
            }
    }

    @OptIn(ExperimentalTime::class)
    fun readPropertyById(id: String): Property? {
        return query.getPropertyById(id)
            .executeAsOneOrNull()
            .let { dbProperty ->
                dbProperty?.let {
                    Property(
                        id = dbProperty.id,
                        title = dbProperty.title,
                        description = dbProperty.description,
                        type = dbProperty.type,
                        saleType = dbProperty.saleType,
                        price = dbProperty.price.toFloat(),
                        locationId = dbProperty.locationId,
                        isActive = dbProperty.isActive == 1L,
                        isFurnished = dbProperty.isFurnished == 1L,
                        furnishingType = dbProperty.furnishingType,
                        bedrooms = dbProperty.bedrooms.toInt(),
                        bathrooms = dbProperty.bathrooms.toInt(),
                        area = dbProperty.area.toFloat(),
                        featuredImg = dbProperty.featuredImg,
                        additionalImgs = dbProperty.additionalImgs?.let { Json.decodeFromString<List<String>>(it) } ?: emptyList(),
                        videoUrl = dbProperty.videoUrl,
                        floorPlanUrl = dbProperty.floorPlanUrl,
                        amenities = dbProperty.amenities?.let { Json.decodeFromString<List<String>>(it) } ?: emptyList(),
                        utilities = dbProperty.utilities?.let { Json.decodeFromString<List<String>>(it) } ?: emptyList(),
                        isVerified = dbProperty.isVerified == 1L,
                        isFeatured = dbProperty.isFeatured == 1L,
                        isSold = dbProperty.isSold == 1L,
                        isRented = dbProperty.isRented == 1L,
                        isShared = dbProperty.isShared == 1L,
                        isReserved = dbProperty.isReserved == 1L,
                        isArchived = dbProperty.isArchived == 1L,
                        archivedAt = dbProperty.archivedAt?.toInstantSafe(),
                        archivedReason = dbProperty.archivedReason,
                        archivedBy = dbProperty.archivedBy,
                        upi = dbProperty.upi,
                        ownerId = dbProperty.ownerId,
                        categoryId = dbProperty.categoryId,
                        placeId = dbProperty.placeId,
                        createdAt = dbProperty.createdAt.toInstantSafe(),
                        updatedAt = dbProperty.updatedAt.toInstantSafe()

                    )
                }
            }
    }

    @OptIn(ExperimentalTime::class)
    fun insertAllProperties(properties: List<Property>) {
        println("INFO: Inserting properties")
        query.transaction {
            properties.forEach { property ->
                query.insertProperty(
                    id = property.id,
                    title = property.title,
                    description = property.description,
                    type = property.type,
                    saleType = property.saleType,
                    price = property.price.toDouble(),
                    locationId = property.locationId,
                    isActive = if (property.isActive) 1L else 0L,
                    isFurnished = if (property.isFurnished) 1L else 0L,
                    furnishingType = property.furnishingType.toString(),
                    bedrooms = property.bedrooms.toLong(),
                    bathrooms = property.bathrooms.toLong(),
                    area = property.area.toDouble(),
                    featuredImg = property.featuredImg,
                    additionalImgs = Json.encodeToString(property.additionalImgs),
                    videoUrl = property.videoUrl,
                    floorPlanUrl = property.floorPlanUrl,
                    amenities = Json.encodeToString(property.amenities),
                    utilities = Json.encodeToString(property.utilities),
                    isVerified = if (property.isVerified) 1L else 0L,
                    isFeatured = if (property.isFeatured) 1L else 0L,
                    isSold = if (property.isSold) 1L else 0L,
                    isRented = if (property.isRented) 1L else 0L,
                    isShared = if (property.isShared) 1L else 0L,
                    isReserved = if (property.isReserved) 1L else 0L,
                    isArchived = if (property.isArchived) 1L else 0L,
                    archivedAt = property.archivedAt?.toString(),
                    archivedReason = property.archivedReason,
                    archivedBy = property.archivedBy,
                    upi = property.upi,
                    ownerId = property.ownerId,
                    categoryId = property.categoryId,
                    placeId = property.placeId,
                )
            }
        }
    }

    fun removeAllProperties() {
        query.removeAllProperties()
    }

    fun getToken() =
        authTokenQueries.getToken().executeAsOneOrNull()

    fun clearToken() =
        authTokenQueries.clearToken()

    fun insertToken(
        accessToken: String,
        refreshToken: String?,
        expiresAt: String
    ) {
        if (accessToken.isBlank()) return

        authTokenQueries.transaction {
            authTokenQueries.clearToken()
            authTokenQueries.insertToken(
                accessToken = accessToken,
                refreshToken = refreshToken ?: "",
                expiresAt = expiresAt
            )
        }
    }

}

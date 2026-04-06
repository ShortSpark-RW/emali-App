package com.shortspark.emaliestates.data.repository

import com.shortspark.emaliestates.data.local.LocalDatabase
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.domain.PropertyType
import com.shortspark.emaliestates.domain.SaleType
import com.shortspark.emaliestates.domain.FurnishingType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import toInstantSafe
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class PropertyRepository(
    private val localDatabase: LocalDatabase
) {
    private val queries = localDatabase.propertyQueries

    suspend fun getLocalProperties(): List<Property> = withContext(Dispatchers.IO) {
        queries.readAllProperties().executeAsList().map { row ->
            Property(
                id = row.id,
                title = row.title,
                description = row.description,
                type = PropertyType.valueOf(row.type),
                saleType = SaleType.valueOf(row.saleType),
                price = row.price.toFloat(),
                locationId = row.locationId,
                isActive = row.isActive == 1L,
                isFurnished = row.isFurnished == 1L,
                furnishingType = FurnishingType.valueOf(row.furnishingType),
                bedrooms = row.bedrooms.toInt(),
                bathrooms = row.bathrooms.toInt(),
                area = row.area.toFloat(),
                featuredImg = row.featuredImg,
                additionalImgs = row.additionalImgs?.let { Json.decodeFromString<List<String>>(it) } ?: emptyList(),
                videoUrl = row.videoUrl,
                floorPlanUrl = row.floorPlanUrl,
                amenities = row.amenities?.let { Json.decodeFromString<List<String>>(it) } ?: emptyList(),
                utilities = row.utilities?.let { Json.decodeFromString<List<String>>(it) } ?: emptyList(),
                isVerified = row.isVerified == 1L,
                isFeatured = row.isFeatured == 1L,
                isSold = row.isSold == 1L,
                isRented = row.isRented == 1L,
                isShared = row.isShared == 1L,
                isReserved = row.isReserved == 1L,
                isArchived = row.isArchived == 1L,
                archivedAt = row.archivedAt?.toInstantSafe(),
                archivedReason = row.archivedReason,
                archivedBy = row.archivedBy,
                upi = row.upi,
                ownerId = row.ownerId,
                categoryId = row.categoryId,
                placeId = row.placeId,
                placeName = row.placeName,
                categoryName = row.categoryName,
                ownerName = row.ownerName,
                ownerPhone = row.ownerPhone,
                ownerProfileImg = row.ownerProfileImg,
                latitude = row.latitude?.toFloat(),
                longitude = row.longitude?.toFloat(),
                address = row.address,
                createdAt = row.createdAt.toInstantSafe(),
                updatedAt = row.updatedAt.toInstantSafe()
            )
        }
    }

    suspend fun getPropertyById(id: String): Property? = withContext(Dispatchers.IO) {
        queries.getPropertyById(id).executeAsOneOrNull()?.let { row ->
            Property(
                id = row.id,
                title = row.title,
                description = row.description,
                type = PropertyType.valueOf(row.type),
                saleType = SaleType.valueOf(row.saleType),
                price = row.price.toFloat(),
                locationId = row.locationId,
                isActive = row.isActive == 1L,
                isFurnished = row.isFurnished == 1L,
                furnishingType = FurnishingType.valueOf(row.furnishingType),
                bedrooms = row.bedrooms.toInt(),
                bathrooms = row.bathrooms.toInt(),
                area = row.area.toFloat(),
                featuredImg = row.featuredImg,
                additionalImgs = row.additionalImgs?.let { Json.decodeFromString<List<String>>(it) } ?: emptyList(),
                videoUrl = row.videoUrl,
                floorPlanUrl = row.floorPlanUrl,
                amenities = row.amenities?.let { Json.decodeFromString<List<String>>(it) } ?: emptyList(),
                utilities = row.utilities?.let { Json.decodeFromString<List<String>>(it) } ?: emptyList(),
                isVerified = row.isVerified == 1L,
                isFeatured = row.isFeatured == 1L,
                isSold = row.isSold == 1L,
                isRented = row.isRented == 1L,
                isShared = row.isShared == 1L,
                isReserved = row.isReserved == 1L,
                isArchived = row.isArchived == 1L,
                archivedAt = row.archivedAt?.toInstantSafe(),
                archivedReason = row.archivedReason,
                archivedBy = row.archivedBy,
                upi = row.upi,
                ownerId = row.ownerId,
                categoryId = row.categoryId,
                placeId = row.placeId,
                placeName = row.placeName,
                categoryName = row.categoryName,
                ownerName = row.ownerName,
                ownerPhone = row.ownerPhone,
                ownerProfileImg = row.ownerProfileImg,
                latitude = row.latitude?.toFloat(),
                longitude = row.longitude?.toFloat(),
                address = row.address,
                createdAt = row.createdAt.toInstantSafe(),
                updatedAt = row.updatedAt.toInstantSafe()
            )
        }
    }

    suspend fun saveProperties(properties: List<Property>) = withContext(Dispatchers.IO) {
        queries.transaction {
            properties.forEach { property ->
                queries.insertProperty(
                    id = property.id,
                    title = property.title,
                    description = property.description,
                    type = property.type.toString(),
                    saleType = property.saleType.toString(),
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
                    placeName = property.placeName,
                    categoryName = property.categoryName,
                    ownerName = property.ownerName,
                    ownerPhone = property.ownerPhone,
                    ownerProfileImg = property.ownerProfileImg,
                    latitude = property.latitude?.toDouble(),
                    longitude = property.longitude?.toDouble(),
                    address = property.address,
                    createdAt = property.createdAt.toString(),
                    updatedAt = property.updatedAt.toString()
                )
            }
        }
    }

    suspend fun clearProperties() = withContext(Dispatchers.IO) {
        queries.removeAllProperties()
    }
}

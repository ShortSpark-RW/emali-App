package com.shortspark.emaliestates.data.local

import app.cash.sqldelight.db.SqlDriver
import com.shortspark.emaliestates.database.PropertyDatabase
import com.shortspark.emaliestates.domain.Location
import com.shortspark.emaliestates.domain.auth.User
import com.shortspark.emaliestates.domain.Category
import com.shortspark.emaliestates.domain.Place
import com.shortspark.emaliestates.domain.Reservation
import com.shortspark.emaliestates.domain.Favorite
import com.shortspark.emaliestates.domain.Identification
import com.shortspark.emaliestates.domain.RentalPeriod
import com.shortspark.emaliestates.domain.RentalType
import com.shortspark.emaliestates.domain.Notification
import com.shortspark.emaliestates.domain.Testimonial
import com.shortspark.emaliestates.domain.FAQ
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.domain.location.Province
import com.shortspark.emaliestates.domain.location.District
import com.shortspark.emaliestates.domain.location.Sector
import com.shortspark.emaliestates.domain.location.Cell
import com.shortspark.emaliestates.domain.location.Village
import com.shortspark.emaliestates.domain.ReservationStatus
import com.shortspark.emaliestates.domain.NotificationType
import com.shortspark.emaliestates.domain.IdentificationType
import com.shortspark.emaliestates.domain.VerificationStatus
import com.shortspark.emaliestates.util.helpers.logger
import com.shortspark.emaliestates.util.helpers.loggerFor
import com.shortspark.emaliestates.util.helpers.toInstantSafe
import kotlinx.serialization.json.Json
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.runBlocking

interface DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

@OptIn(ExperimentalTime::class)
class LocalDatabase(
    databaseDriverFactory: DatabaseDriverFactory
) {
    private val logger = loggerFor(this)
    private val database = PropertyDatabase(
        databaseDriverFactory.createDriver()
    )

    // Property queries
    private val propertyQuery = database.propertyDatabaseQueries
    val authTokenQueries = database.authTokenQueries

    // All other query interfaces
    val userQueries = database.userQueries
    val locationQueries = database.locationQueries
    val provinceQueries = database.provinceQueries
    val districtQueries = database.districtQueries
    val sectorQueries = database.sectorQueries
    val cellQueries = database.cellQueries
    val villageQueries = database.villageQueries
    val categoryQueries = database.categoryQueries
    val placeQueries = database.placeQueries
    val reservationQueries = database.reservationQueries
    val favoriteQueries = database.favoriteQueries
    val identificationQueries = database.identificationQueries
    val rentalPeriodQueries = database.rentalPeriodQueries
    val notificationQueries = database.notificationQueries
    val testimonialQueries = database.testimonialQueries
    val fAQQueries = database.fAQQueries

    // ==================== Property Operations ====================

    @OptIn(ExperimentalTime::class)
    fun readAllProperties(): List<Property> {
        return propertyQuery.readAllProperties()
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
        return propertyQuery.getPropertyById(id)
            .executeAsOneOrNull()
            ?.let { dbProperty ->
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

    fun insertAllProperties(properties: List<Property>) {
        logger.info("Inserting ${properties.size} properties into local database")
        propertyQuery.transaction {
            properties.forEach { property ->
                propertyQuery.insertProperty(
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
        propertyQuery.removeAllProperties()
    }

    fun getToken() =
        authTokenQueries.getToken().executeAsOneOrNull()

    fun clearToken() =
        authTokenQueries.clearToken()

    fun insertToken(
        accessToken: String,
        refreshToken: String?,
        expiresAt: String? = null,
        userId: String? = null
    ) {
        if (accessToken.isBlank()) return

        authTokenQueries.transaction {
            authTokenQueries.clearToken()
            authTokenQueries.insertToken(
                accessToken = accessToken,
                refreshToken = refreshToken ?: "",
                expiresAt = expiresAt,
                userId = userId ?: ""
            )
        }
    }

    // ==================== User Operations ====================

    fun insertUser(user: User) {
        userQueries.insertUser(
            id = user.id,
            email = user.email,
            phone = user.phone,
            username = user.username,
            name = user.name,
            role = user.role,
            gender = user.gender,
            dob = user.dob,
            address = user.address,
            country = user.country,
            province = user.province,
            district = user.district,
            whatsapp = user.whatsapp,
            profileImg = user.profileImg,
            coverImg = user.coverImg,
            language = user.language,
            isActive = if (user.isActive == true) 1L else 0L,
            isVerified = if (user.isVerified == true) 1L else 0L,
            createdAt = user.createdAt?.toString(),
            updatedAt = user.updatedAt?.toString()
        )
    }

    fun getUserById(id: String): User? {
        return userQueries.getUserById(id).executeAsOneOrNull()?.let { mapToUser(it) }
    }

    fun getUserByEmail(email: String): User? {
        return userQueries.getUserByEmail(email).executeAsOneOrNull()?.let { mapToUser(it) }
    }

    fun getUserByUsername(username: String): User? {
        return userQueries.getUserByUsername(username).executeAsOneOrNull()?.let { mapToUser(it) }
    }

    fun getAllUsers(): List<User> {
        return userQueries.getAllUsers().executeAsList().map { mapToUser(it) }
    }

    fun updateUser(user: User) {
        userQueries.updateUser(
            email = user.email,
            phone = user.phone,
            username = user.username,
            name = user.name,
            role = user.role,
            gender = user.gender,
            dob = user.dob,
            address = user.address,
            country = user.country,
            province = user.province,
            district = user.district,
            whatsapp = user.whatsapp,
            profileImg = user.profileImg,
            coverImg = user.coverImg,
            language = user.language,
            isActive = if (user.isActive == true) 1L else 0L,
            isVerified = if (user.isVerified == true) 1L else 0L,
            updatedAt = user.updatedAt?.toString(),
            id = user.id
        )
    }

    fun deleteUser(id: String) {
        userQueries.deleteUser(id)
    }

    @OptIn(ExperimentalTime::class)
    private fun mapToUser(db: com.shortspark.User): User {
        return User(
            id = db.id,
            email = db.email,
            phone = db.phone,
            username = db.username,
            name = db.name,
            role = db.role,
            gender = db.gender,
            dob = db.dob,
            address = db.address,
            country = db.country,
            province = db.province,
            district = db.district,
            whatsapp = db.whatsapp,
            profileImg = db.profileImg,
            coverImg = db.coverImg,
            language = db.language,
            isActive = db.isActive == 1L,
            isVerified = db.isVerified == 1L,
            createdAt = db.createdAt?.toInstantSafe(),
            updatedAt = db.updatedAt?.toInstantSafe()
        )
    }

    // ==================== Location Operations ====================

    fun insertLocation(location: Location) {
        locationQueries.insertLocation(
            id = location.id,
            provinceId = location.provinceId,
            districtId = location.districtId,
            sectorId = location.sectorId,
            cellId = location.cellId,
            villageId = location.villageId,
            latitude = location.latitude,
            longitude = location.longitude,
            address = location.address,
            createdAt = location.createdAt?.toString(),
            updatedAt = location.updatedAt?.toString()
        )
    }

    fun getLocationById(id: String): Location? {
        return locationQueries.getLocationById(id).executeAsOneOrNull()?.let { mapToLocation(it) }
    }

    fun getAllLocations(): List<Location> {
        return locationQueries.getAllLocations().executeAsList().map { mapToLocation(it) }
    }

    fun updateLocation(location: Location) {
        locationQueries.updateLocation(
            provinceId = location.provinceId,
            districtId = location.districtId,
            sectorId = location.sectorId,
            cellId = location.cellId,
            villageId = location.villageId,
            latitude = location.latitude,
            longitude = location.longitude,
            address = location.address,
            updatedAt = location.updatedAt?.toString(),
            id = location.id
        )
    }

    fun deleteLocation(id: String) {
        locationQueries.deleteLocation(id)
    }

    @OptIn(ExperimentalTime::class)
    private fun mapToLocation(db: com.shortspark.Location): Location {
        return Location(
            id = db.id,
            provinceId = db.provinceId,
            districtId = db.districtId,
            sectorId = db.sectorId,
            cellId = db.cellId,
            villageId = db.villageId,
            latitude = db.latitude,
            longitude = db.longitude,
            address = db.address,
            createdAt = db.createdAt?.toInstantSafe(),
            updatedAt = db.updatedAt?.toInstantSafe()
        )
    }

    // ==================== Province Operations ====================

    fun insertProvince(province: Province) {
        provinceQueries.insertProvince(
            id = province.id,
            name = province.name,
            code = province.code,
            createdAt = province.createdAt?.toString(),
            updatedAt = province.updatedAt?.toString()
        )
    }

    fun getProvinceById(id: String): Province? {
        return provinceQueries.getProvinceById(id).executeAsOneOrNull()?.let { mapToProvince(it) }
    }

    fun getAllProvinces(): List<Province> {
        return provinceQueries.getAllProvinces().executeAsList().map { mapToProvince(it) }
    }

    fun updateProvince(province: Province) {
        provinceQueries.updateProvince(
            name = province.name,
            code = province.code,
            updatedAt = province.updatedAt?.toString(),
            id = province.id
        )
    }

    fun deleteProvince(id: String) {
        provinceQueries.deleteProvince(id)
    }

    @OptIn(ExperimentalTime::class)
    private fun mapToProvince(db: com.shortspark.Province): Province {
        return Province(
            id = db.id,
            name = db.name,
            code = db.code,
            createdAt = db.createdAt?.toInstantSafe(),
            updatedAt = db.updatedAt?.toInstantSafe()
        )
    }

    // ==================== District Operations ====================

    fun insertDistrict(district: District) {
        districtQueries.insertDistrict(
            id = district.id,
            name = district.name,
            provinceId = district.provinceId,
            code = district.code,
            createdAt = district.createdAt?.toString(),
            updatedAt = district.updatedAt?.toString()
        )
    }

    fun getDistrictById(id: String): District? {
        return districtQueries.getDistrictById(id).executeAsOneOrNull()?.let { mapToDistrict(it) }
    }

    fun getDistrictsByProvinceId(provinceId: String): List<District> {
        return districtQueries.getDistrictsByProvinceId(provinceId).executeAsList().map { mapToDistrict(it) }
    }

    fun getAllDistricts(): List<District> {
        return districtQueries.getAllDistricts().executeAsList().map { mapToDistrict(it) }
    }

    fun updateDistrict(district: District) {
        districtQueries.updateDistrict(
            name = district.name,
            provinceId = district.provinceId,
            code = district.code,
            updatedAt = district.updatedAt?.toString(),
            id = district.id
        )
    }

    fun deleteDistrict(id: String) {
        districtQueries.deleteDistrict(id)
    }

    @OptIn(ExperimentalTime::class)
    private fun mapToDistrict(db: com.shortspark.District): District {
        return District(
            id = db.id,
            name = db.name,
            provinceId = db.provinceId,
            code = db.code,
            createdAt = db.createdAt?.toInstantSafe(),
            updatedAt = db.updatedAt?.toInstantSafe()
        )
    }

    // ==================== Sector Operations ====================

    fun insertSector(sector: Sector) {
        sectorQueries.insertSector(
            id = sector.id,
            name = sector.name,
            districtId = sector.districtId,
            code = sector.code,
            createdAt = sector.createdAt?.toString(),
            updatedAt = sector.updatedAt?.toString()
        )
    }

    fun getSectorById(id: String): Sector? {
        return sectorQueries.getSectorById(id).executeAsOneOrNull()?.let { mapToSector(it) }
    }

    fun getSectorsByDistrictId(districtId: String): List<Sector> {
        return sectorQueries.getSectorsByDistrictId(districtId).executeAsList().map { mapToSector(it) }
    }

    fun getAllSectors(): List<Sector> {
        return sectorQueries.getAllSectors().executeAsList().map { mapToSector(it) }
    }

    fun updateSector(sector: Sector) {
        sectorQueries.updateSector(
            name = sector.name,
            districtId = sector.districtId,
            code = sector.code,
            updatedAt = sector.updatedAt?.toString(),
            id = sector.id
        )
    }

    fun deleteSector(id: String) {
        sectorQueries.deleteSector(id)
    }

    @OptIn(ExperimentalTime::class)
    private fun mapToSector(db: com.shortspark.Sector): Sector {
        return Sector(
            id = db.id,
            name = db.name,
            districtId = db.districtId,
            code = db.code,
            createdAt = db.createdAt?.toInstantSafe(),
            updatedAt = db.updatedAt?.toInstantSafe()
        )
    }

    // ==================== Cell Operations ====================

    fun insertCell(cell: Cell) {
        cellQueries.insertCell(
            id = cell.id,
            name = cell.name,
            sectorId = cell.sectorId,
            code = cell.code,
            createdAt = cell.createdAt?.toString(),
            updatedAt = cell.updatedAt?.toString()
        )
    }

    fun getCellById(id: String): Cell? {
        return cellQueries.getCellById(id).executeAsOneOrNull()?.let { mapToCell(it) }
    }

    fun getCellsBySectorId(sectorId: String): List<Cell> {
        return cellQueries.getCellsBySectorId(sectorId).executeAsList().map { mapToCell(it) }
    }

    fun getAllCells(): List<Cell> {
        return cellQueries.getAllCells().executeAsList().map { mapToCell(it) }
    }

    fun updateCell(cell: Cell) {
        cellQueries.updateCell(
            name = cell.name,
            sectorId = cell.sectorId,
            code = cell.code,
            updatedAt = cell.updatedAt?.toString(),
            id = cell.id
        )
    }

    fun deleteCell(id: String) {
        cellQueries.deleteCell(id)
    }

    @OptIn(ExperimentalTime::class)
    private fun mapToCell(db: com.shortspark.Cell): Cell {
        return Cell(
            id = db.id,
            name = db.name,
            sectorId = db.sectorId,
            code = db.code,
            createdAt = db.createdAt?.toInstantSafe(),
            updatedAt = db.updatedAt?.toInstantSafe()
        )
    }

    // ==================== Village Operations ====================

    fun insertVillage(village: Village) {
        villageQueries.insertVillage(
            id = village.id,
            name = village.name,
            cellId = village.cellId,
            code = village.code,
            createdAt = village.createdAt?.toString(),
            updatedAt = village.updatedAt?.toString()
        )
    }

    fun getVillageById(id: String): Village? {
        return villageQueries.getVillageById(id).executeAsOneOrNull()?.let { mapToVillage(it) }
    }

    fun getVillagesByCellId(cellId: String): List<Village> {
        return villageQueries.getVillagesByCellId(cellId).executeAsList().map { mapToVillage(it) }
    }

    fun getAllVillages(): List<Village> {
        return villageQueries.getAllVillages().executeAsList().map { mapToVillage(it) }
    }

    fun updateVillage(village: Village) {
        villageQueries.updateVillage(
            name = village.name,
            cellId = village.cellId,
            code = village.code,
            updatedAt = village.updatedAt?.toString(),
            id = village.id
        )
    }

    fun deleteVillage(id: String) {
        villageQueries.deleteVillage(id)
    }

    @OptIn(ExperimentalTime::class)
    private fun mapToVillage(db: com.shortspark.Village): Village {
        return Village(
            id = db.id,
            name = db.name,
            cellId = db.cellId,
            code = db.code,
            createdAt = db.createdAt?.toInstantSafe(),
            updatedAt = db.updatedAt?.toInstantSafe()
        )
    }

    // ==================== Category Operations ====================

    fun insertCategory(category: Category) {
        categoryQueries.insertCategory(
            id = category.id,
            name = category.name,
            description = category.description,
            icon = category.icon,
            color = category.color,
            sortOrder = category.order.toLong(),
            isActive = if (category.isActive) 1L else 0L,
            createdAt = category.createdAt?.toString(),
            updatedAt = category.updatedAt?.toString()
        )
    }

    fun getCategoryById(id: String): Category? {
        return categoryQueries.getCategoryById(id).executeAsOneOrNull()?.let { mapToCategory(it) }
    }

    fun getAllCategories(): List<Category> {
        return categoryQueries.getAllCategories().executeAsList().map { mapToCategory(it) }
    }

    fun getActiveCategories(): List<Category> {
        return categoryQueries.getActiveCategories().executeAsList().map { mapToCategory(it) }
    }

    fun updateCategory(category: Category) {
        categoryQueries.updateCategory(
            name = category.name,
            description = category.description,
            icon = category.icon,
            color = category.color,
            sortOrder = category.order.toLong(),
            isActive = if (category.isActive) 1L else 0L,
            updatedAt = category.updatedAt?.toString(),
            id = category.id
        )
    }

    fun deleteCategory(id: String) {
        categoryQueries.deleteCategory(id)
    }

    @OptIn(ExperimentalTime::class)
    private fun mapToCategory(db: com.shortspark.Category): Category {
        return Category(
            id = db.id,
            name = db.name,
            description = db.description,
            icon = db.icon,
            color = db.color,
            order = db.sortOrder.toInt(),
            isActive = db.isActive == 1L,
            createdAt = db.createdAt?.toInstantSafe(),
            updatedAt = db.updatedAt?.toInstantSafe()
        )
    }

    // ==================== Place Operations ====================

    fun insertPlace(place: Place) {
        placeQueries.insertPlace(
            id = place.id,
            name = place.name,
            province = place.province,
            district = place.district,
            sector = place.sector,
            cell = place.cell,
            village = place.village,
            postalCode = place.postalCode,
            image = place.image,
            sortOrder = place.order.toLong(),
            isActive = if (place.isActive) 1L else 0L,
            createdAt = place.createdAt?.toString(),
            updatedAt = place.updatedAt?.toString()
        )
    }

    fun getPlaceById(id: String): Place? {
        return placeQueries.getPlaceById(id).executeAsOneOrNull()?.let { mapToPlace(it) }
    }

    fun getAllPlaces(): List<Place> {
        return placeQueries.getAllPlaces().executeAsList().map { mapToPlace(it) }
    }

    fun getActivePlaces(): List<Place> {
        return placeQueries.getActivePlaces().executeAsList().map { mapToPlace(it) }
    }

    fun getPlacesByProvince(province: String): List<Place> {
        return placeQueries.getPlacesByProvince(province).executeAsList().map { mapToPlace(it) }
    }

    fun getPlacesByDistrict(district: String): List<Place> {
        return placeQueries.getPlacesByDistrict(district).executeAsList().map { mapToPlace(it) }
    }

    fun updatePlace(place: Place) {
        placeQueries.updatePlace(
            name = place.name,
            province = place.province,
            district = place.district,
            sector = place.sector,
            cell = place.cell,
            village = place.village,
            postalCode = place.postalCode,
            image = place.image,
            sortOrder = place.order.toLong(),
            isActive = if (place.isActive) 1L else 0L,
            updatedAt = place.updatedAt?.toString(),
            id = place.id
        )
    }

    fun deletePlace(id: String) {
        placeQueries.deletePlace(id)
    }

    @OptIn(ExperimentalTime::class)
    private fun mapToPlace(db: com.shortspark.Place): Place {
        return Place(
            id = db.id,
            name = db.name,
            province = db.province,
            district = db.district,
            sector = db.sector,
            cell = db.cell,
            village = db.village,
            postalCode = db.postalCode,
            image = db.image,
            order = db.sortOrder.toInt(),
            isActive = db.isActive == 1L,
            createdAt = db.createdAt?.toInstantSafe(),
            updatedAt = db.updatedAt?.toInstantSafe()
        )
    }

    // ==================== Reservation Operations ====================

    fun insertReservation(reservation: Reservation) {
        reservationQueries.insertReservation(
            id = reservation.id,
            userId = reservation.userId,
            propertyId = reservation.propertyId,
            shareId = reservation.shareId,
            status = reservation.status.name,
            reservedAt = reservation.reservedAt.toString(),
            expiresAt = reservation.expiresAt.toString(),
            createdAt = reservation.createdAt?.toString(),
            updatedAt = reservation.updatedAt?.toString()
        )
    }

    fun getReservationById(id: String): Reservation? {
        return reservationQueries.getReservationById(id).executeAsOneOrNull()?.let { mapToReservation(it) }
    }

    fun getReservationsByUserId(userId: String): List<Reservation> {
        return reservationQueries.getReservationsByUserId(userId).executeAsList().map { mapToReservation(it) }
    }

    fun getReservationsByPropertyId(propertyId: String): List<Reservation> {
        return reservationQueries.getReservationsByPropertyId(propertyId).executeAsList().map { mapToReservation(it) }
    }

    fun getActiveReservations(): List<Reservation> {
        return reservationQueries.getActiveReservations().executeAsList().map { mapToReservation(it) }
    }

    fun updateReservation(reservation: Reservation) {
        reservationQueries.updateReservation(
            status = reservation.status.name,
            expiresAt = reservation.expiresAt.toString(),
            updatedAt = reservation.updatedAt?.toString(),
            id = reservation.id
        )
    }

    fun deleteReservation(id: String) {
        reservationQueries.deleteReservation(id)
    }

    @OptIn(ExperimentalTime::class)
    private fun mapToReservation(db: com.shortspark.Reservation): Reservation {
        return Reservation(
            id = db.id,
            userId = db.userId,
            propertyId = db.propertyId,
            shareId = db.shareId,
            status = ReservationStatus.valueOf(db.status),
            reservedAt = db.reservedAt.toInstantSafe(),
            expiresAt = db.expiresAt.toInstantSafe(),
            createdAt = db.createdAt?.toInstantSafe(),
            updatedAt = db.updatedAt?.toInstantSafe()
        )
    }

    // ==================== Favorite Operations ====================

    fun insertFavorite(favorite: Favorite) {
        favoriteQueries.insertFavorite(
            id = favorite.id,
            userId = favorite.userId,
            propertyId = favorite.propertyId,
            createdAt = favorite.createdAt.toString()
        )
    }

    fun getFavoriteById(id: String): Favorite? {
        return favoriteQueries.getFavoriteById(id).executeAsOneOrNull()?.let { mapToFavorite(it) }
    }

    fun getFavoritesByUserId(userId: String): List<Favorite> {
        return favoriteQueries.getFavoritesByUserId(userId).executeAsList().map { mapToFavorite(it) }
    }

    fun getFavoriteByUserAndProperty(userId: String, propertyId: String): Favorite? {
        return favoriteQueries.getFavoriteByUserAndProperty(userId, propertyId).executeAsOneOrNull()?.let { mapToFavorite(it) }
    }

    fun deleteFavorite(id: String) {
        favoriteQueries.deleteFavorite(id)
    }

    fun deleteFavoriteByUserAndProperty(userId: String, propertyId: String) {
        favoriteQueries.deleteFavoriteByUserAndProperty(userId, propertyId)
    }

    @OptIn(ExperimentalTime::class)
    private fun mapToFavorite(db: com.shortspark.Favorite): Favorite {
        return Favorite(
            id = db.id,
            userId = db.userId,
            propertyId = db.propertyId,
            createdAt = db.createdAt.toInstantSafe()
        )
    }

    // ==================== Identification Operations ====================

    fun insertIdentification(identification: Identification) {
        identificationQueries.insertIdentification(
            id = identification.id,
            userId = identification.userId,
            type = identification.type.name,
            countryOfIssue = identification.countryOfIssue,
            documentUrl = identification.documentUrl,
            extractedText = identification.extractedText,
            verificationStatus = identification.verificationStatus.name,
            nationalId = identification.nationalId,
            passport = identification.passport,
            drivingLicense = identification.drivingLicense,
            verified = if (identification.verified) 1L else 0L,
            verificationDate = identification.verificationDate?.toString(),
            faceVerified = if (identification.faceVerified) 1L else 0L,
            createdAt = identification.createdAt?.toString(),
            updatedAt = identification.updatedAt?.toString()
        )
    }

    fun getIdentificationById(id: String): Identification? {
        return identificationQueries.getIdentificationById(id).executeAsOneOrNull()?.let { mapToIdentification(it) }
    }

    fun getIdentificationByUserId(userId: String): Identification? {
        return identificationQueries.getIdentificationByUserId(userId).executeAsOneOrNull()?.let { mapToIdentification(it) }
    }

    fun getAllIdentifications(): List<Identification> {
        return identificationQueries.getAllIdentifications().executeAsList().map { mapToIdentification(it) }
    }

    fun getPendingVerifications(): List<Identification> {
        return identificationQueries.getPendingVerifications().executeAsList().map { mapToIdentification(it) }
    }

    fun updateIdentification(identification: Identification) {
        identificationQueries.updateIdentification(
            type = identification.type.name,
            countryOfIssue = identification.countryOfIssue,
            documentUrl = identification.documentUrl,
            extractedText = identification.extractedText,
            verificationStatus = identification.verificationStatus.name,
            nationalId = identification.nationalId,
            passport = identification.passport,
            drivingLicense = identification.drivingLicense,
            verified = if (identification.verified) 1L else 0L,
            verificationDate = identification.verificationDate?.toString(),
            faceVerified = if (identification.faceVerified) 1L else 0L,
            updatedAt = identification.updatedAt?.toString(),
            id = identification.id
        )
    }

    fun deleteIdentification(id: String) {
        identificationQueries.deleteIdentification(id)
    }

    @OptIn(ExperimentalTime::class)
    private fun mapToIdentification(db: com.shortspark.Identification): Identification {
        return Identification(
            id = db.id,
            userId = db.userId,
            type = IdentificationType.valueOf(db.type),
            countryOfIssue = db.countryOfIssue,
            documentUrl = db.documentUrl,
            extractedText = db.extractedText,
            verificationStatus = VerificationStatus.valueOf(db.verificationStatus),
            nationalId = db.nationalId,
            passport = db.passport,
            drivingLicense = db.drivingLicense,
            verified = db.verified == 1L,
            verificationDate = db.verificationDate?.toInstantSafe(),
            faceVerified = db.faceVerified == 1L,
            createdAt = db.createdAt?.toInstantSafe(),
            updatedAt = db.updatedAt?.toInstantSafe()
        )
    }

    // ==================== Rental Period Operations ====================

    fun insertRentalPeriod(rentalPeriod: RentalPeriod) {
        rentalPeriodQueries.insertRentalPeriod(
            id = rentalPeriod.id,
            propertyId = rentalPeriod.propertyId,
            periodType = rentalPeriod.periodType.name,
            price = rentalPeriod.price,
            minNights = rentalPeriod.minNights?.toLong(),
            maxNights = rentalPeriod.maxNights?.toLong(),
            availableFrom = rentalPeriod.availableFrom?.toString(),
            availableTo = rentalPeriod.availableTo?.toString(),
            isActive = if (rentalPeriod.isActive) 1L else 0L,
            createdAt = rentalPeriod.createdAt?.toString(),
            updatedAt = rentalPeriod.updatedAt?.toString()
        )
    }

    fun getRentalPeriodById(id: String): RentalPeriod? {
        return rentalPeriodQueries.getRentalPeriodById(id).executeAsOneOrNull()?.let { mapToRentalPeriod(it) }
    }

    fun getRentalPeriodsByPropertyId(propertyId: String): List<RentalPeriod> {
        return rentalPeriodQueries.getRentalPeriodsByPropertyId(propertyId).executeAsList().map { mapToRentalPeriod(it) }
    }

    fun getActiveRentalPeriodsByPropertyId(propertyId: String): List<RentalPeriod> {
        return rentalPeriodQueries.getActiveRentalPeriodsByPropertyId(propertyId).executeAsList().map { mapToRentalPeriod(it) }
    }

    fun getAllRentalPeriods(): List<RentalPeriod> {
        return rentalPeriodQueries.getAllRentalPeriods().executeAsList().map { mapToRentalPeriod(it) }
    }

    fun updateRentalPeriod(rentalPeriod: RentalPeriod) {
        rentalPeriodQueries.updateRentalPeriod(
            periodType = rentalPeriod.periodType.name,
            price = rentalPeriod.price,
            minNights = rentalPeriod.minNights?.toLong(),
            maxNights = rentalPeriod.maxNights?.toLong(),
            availableFrom = rentalPeriod.availableFrom?.toString(),
            availableTo = rentalPeriod.availableTo?.toString(),
            isActive = if (rentalPeriod.isActive) 1L else 0L,
            updatedAt = rentalPeriod.updatedAt?.toString(),
            id = rentalPeriod.id
        )
    }

    fun deleteRentalPeriod(id: String) {
        rentalPeriodQueries.deleteRentalPeriod(id)
    }

    @OptIn(ExperimentalTime::class)
    private fun mapToRentalPeriod(db: com.shortspark.RentalPeriod): RentalPeriod {
        return RentalPeriod(
            id = db.id,
            propertyId = db.propertyId,
            periodType = RentalType.valueOf(db.periodType),
            price = db.price,
            minNights = db.minNights?.toInt(),
            maxNights = db.maxNights?.toInt(),
            availableFrom = db.availableFrom?.toInstantSafe(),
            availableTo = db.availableTo?.toInstantSafe(),
            isActive = db.isActive == 1L,
            createdAt = db.createdAt?.toInstantSafe(),
            updatedAt = db.updatedAt?.toInstantSafe()
        )
    }

    // ==================== Notification Operations ====================

    fun insertNotification(notification: Notification) {
        notificationQueries.insertNotification(
            id = notification.id,
            userId = notification.userId,
            title = notification.title,
            body = notification.body,
            type = notification.type?.name,
            data_ = notification.data?.let { Json.encodeToString(it) },
            isRead = if (notification.isRead) 1L else 0L,
            createdAt = notification.createdAt.toString()
        )
    }

    fun getNotificationById(id: String): Notification? {
        return notificationQueries.getNotificationById(id).executeAsOneOrNull()?.let { mapToNotification(it) }
    }

    fun getNotificationsByUserId(userId: String): List<Notification> {
        return notificationQueries.getNotificationsByUserId(userId).executeAsList().map { mapToNotification(it) }
    }

    fun getUnreadNotificationsByUserId(userId: String): List<Notification> {
        return notificationQueries.getUnreadNotificationsByUserId(userId).executeAsList().map { mapToNotification(it) }
    }

    fun getAllNotifications(): List<Notification> {
        return notificationQueries.getAllNotifications().executeAsList().map { mapToNotification(it) }
    }

    fun markNotificationAsRead(id: String) {
        notificationQueries.markNotificationAsRead(id)
    }

    fun markAllNotificationsAsReadForUser(userId: String) {
        notificationQueries.markAllNotificationsAsReadForUser(userId)
    }

    fun deleteNotification(id: String) {
        notificationQueries.deleteNotification(id)
    }

    private fun mapToNotification(db: com.shortspark.Notification): Notification {
        return Notification(
            id = db.id,
            userId = db.userId,
            title = db.title,
            body = db.body,
            type = db.type?.let { NotificationType.valueOf(it) },
            data = db.data_?.let { Json.decodeFromString<Map<String, String>>(it) },
            isRead = db.isRead == 1L,
            createdAt = db.createdAt.toInstantSafe()
        )
    }

    // ==================== Testimonial Operations ====================

    fun insertTestimonial(testimonial: Testimonial) {
        testimonialQueries.insertTestimonial(
            id = testimonial.id,
            userId = testimonial.userId,
            propertyId = testimonial.propertyId,
            rating = testimonial.rating.toLong(),
            comment = testimonial.comment,
            title = testimonial.title,
            isApproved = if (testimonial.isApproved) 1L else 0L,
            createdAt = testimonial.createdAt?.toString(),
            updatedAt = testimonial.updatedAt?.toString()
        )
    }

    fun getTestimonialById(id: String): Testimonial? {
        return testimonialQueries.getTestimonialById(id).executeAsOneOrNull()?.let { mapToTestimonial(it) }
    }

    fun getTestimonialsByUserId(userId: String): List<Testimonial> {
        return testimonialQueries.getTestimonialsByUserId(userId).executeAsList().map { mapToTestimonial(it) }
    }

    fun getTestimonialsByPropertyId(propertyId: String): List<Testimonial> {
        return testimonialQueries.getTestimonialsByPropertyId(propertyId).executeAsList().map { mapToTestimonial(it) }
    }

    fun getApprovedTestimonialsByPropertyId(propertyId: String): List<Testimonial> {
        return testimonialQueries.getApprovedTestimonialsByPropertyId(propertyId).executeAsList().map { mapToTestimonial(it) }
    }

    fun getAllTestimonials(): List<Testimonial> {
        return testimonialQueries.getAllTestimonials().executeAsList().map { mapToTestimonial(it) }
    }

    fun updateTestimonial(testimonial: Testimonial) {
        testimonialQueries.updateTestimonial(
            rating = testimonial.rating.toLong(),
            comment = testimonial.comment,
            title = testimonial.title,
            isApproved = if (testimonial.isApproved) 1L else 0L,
            updatedAt = testimonial.updatedAt?.toString(),
            id = testimonial.id
        )
    }

    fun deleteTestimonial(id: String) {
        testimonialQueries.deleteTestimonial(id)
    }

    @OptIn(ExperimentalTime::class)
    private fun mapToTestimonial(db: com.shortspark.Testimonial): Testimonial {
        return Testimonial(
            id = db.id,
            userId = db.userId,
            propertyId = db.propertyId,
            rating = db.rating.toInt(),
            comment = db.comment,
            title = db.title,
            isApproved = db.isApproved == 1L,
            createdAt = db.createdAt?.toInstantSafe(),
            updatedAt = db.updatedAt?.toInstantSafe()
        )
    }

    // ==================== FAQ Operations ====================

    fun insertFAQ(faq: FAQ) {
        fAQQueries.insertFAQ(
            id = faq.id,
            question = faq.question,
            answer = faq.answer,
            sortOrder = faq.order.toLong(),
            category = faq.category,
            isActive = if (faq.isActive) 1L else 0L,
            createdAt = faq.createdAt?.toString(),
            updatedAt = faq.updatedAt?.toString()
        )
    }

    fun getFAQById(id: String): FAQ? {
        return fAQQueries.getFAQById(id).executeAsOneOrNull()?.let { mapToFAQ(it) }
    }

    fun getAllFAQs(): List<FAQ> {
        return fAQQueries.getAllFAQs().executeAsList().map { mapToFAQ(it) }
    }

    fun getActiveFAQs(): List<FAQ> {
        return fAQQueries.getActiveFAQs().executeAsList().map { mapToFAQ(it) }
    }

    fun getFAQsByCategory(category: String): List<FAQ> {
        return fAQQueries.getFAQsByCategory(category).executeAsList().map { mapToFAQ(it) }
    }

    fun updateFAQ(faq: FAQ) {
        fAQQueries.updateFAQ(
            question = faq.question,
            answer = faq.answer,
            sortOrder = faq.order.toLong(),
            category = faq.category,
            isActive = if (faq.isActive) 1L else 0L,
            updatedAt = faq.updatedAt?.toString(),
            id = faq.id
        )
    }

    fun deleteFAQ(id: String) {
        fAQQueries.deleteFAQ(id)
    }

    @OptIn(ExperimentalTime::class)
    private fun mapToFAQ(db: com.shortspark.FAQ): FAQ {
        return FAQ(
            id = db.id,
            question = db.question,
            answer = db.answer,
            order = db.sortOrder.toInt(),
            category = db.category,
            isActive = db.isActive == 1L,
            createdAt = db.createdAt?.toInstantSafe(),
            updatedAt = db.updatedAt?.toInstantSafe()
        )
    }

}
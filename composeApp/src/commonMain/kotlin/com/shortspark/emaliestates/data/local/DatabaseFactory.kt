package com.shortspark.emaliestates.data.local

import app.cash.sqldelight.db.SqlDriver
import com.shortspark.emaliestates.database.PropertyDatabase

interface DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

class LocalDatabase(
    databaseDriverFactory: DatabaseDriverFactory
) {
    private val database = PropertyDatabase(
        databaseDriverFactory.createDriver()
    )

    // Expose all query interfaces
    val authTokenQueries = database.authTokenQueries
    val propertyQueries = database.propertyQueries
    val userQueries = database.userQueries
    val categoryQueries = database.categoryQueries
    val locationQueries = database.locationQueries
    val placeQueries = database.placeQueries
    val provinceQueries = database.provinceQueries
    val districtQueries = database.districtQueries
    val sectorQueries = database.sectorQueries
    val cellQueries = database.cellQueries
    val villageQueries = database.villageQueries
    val favoriteQueries = database.favoriteQueries
    val notificationQueries = database.notificationQueries
    val blogQueries = database.blogQueries
    val transactionQueries = database.transactionQueries
    val reservationQueries = database.reservationQueries
    val shareQueries = database.shareQueries
    val testimonialQueries = database.testimonialQueries
    val identificationQueries = database.identificationQueries
    val userSettingQueries = database.userSettingQueries
    val faqQueries = database.faqQueries
}


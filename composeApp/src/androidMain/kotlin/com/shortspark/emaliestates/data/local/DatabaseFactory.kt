package com.shortspark.emaliestates.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.shortspark.emaliestates.database.PropertyDatabase

class AndroidDatabaseDriverFactory(
    private val context: Context
): DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            PropertyDatabase.Schema,
            context,
            "property.db"
        )
    }
}
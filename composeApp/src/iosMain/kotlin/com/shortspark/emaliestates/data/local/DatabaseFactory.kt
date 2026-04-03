package com.shortspark.emaliestates.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.shortspark.emaliestates.database.PropertyDatabase

class IOSDatabaseDriverFactory(): DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            PropertyDatabase.Schema,
            "property.db"
        )
    }
}
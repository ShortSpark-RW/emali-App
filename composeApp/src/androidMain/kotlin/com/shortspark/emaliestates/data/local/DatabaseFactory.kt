package com.shortspark.emaliestates.data.local

import android.content.Context
import android.database.Cursor
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.shortspark.emaliestates.database.PropertyDatabase

class AndroidDatabaseDriverFactory(
    private val context: Context
): DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = PropertyDatabase.Schema,
            context = context,
            name = "property.db",
            callback = object : AndroidSqliteDriver.Callback(PropertyDatabase.Schema) {
                override fun onDowngrade(
                    db: SupportSQLiteDatabase,
                    oldVersion: Int,
                    newVersion: Int
                ) {
                    // Handle downgrade by dropping all tables and recreating the schema.
                    // This is useful during development when schema versions change frequently.
                    val cursor: Cursor = db.query("SELECT name FROM sqlite_master WHERE type='table'", arrayOf<Any?>())
                    val tables = mutableListOf<String>()
                    try {
                        while (cursor.moveToNext()) {
                            val tableName = cursor.getString(0)
                            if (tableName != null && !tableName.startsWith("sqlite_") && tableName != "android_metadata") {
                                tables.add(tableName)
                            }
                        }
                    } finally {
                        cursor.close()
                    }

                    for (table in tables) {
                        db.execSQL("DROP TABLE IF EXISTS \"$table\"")
                    }
                    onCreate(db)
                }
            }
        )
    }
}

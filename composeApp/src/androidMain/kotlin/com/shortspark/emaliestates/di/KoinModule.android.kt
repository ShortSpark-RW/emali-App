package com.shortspark.emaliestates.di

import com.shortspark.emaliestates.data.local.AndroidDatabaseDriverFactory
import com.shortspark.emaliestates.data.local.DatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val targetModule = module {
    single<DatabaseDriverFactory> {
        AndroidDatabaseDriverFactory(androidContext())
    }
}
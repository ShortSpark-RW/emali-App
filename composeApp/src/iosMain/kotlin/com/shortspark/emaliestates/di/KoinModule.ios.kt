package com.shortspark.emaliestates.di

import com.shortspark.emaliestates.data.local.DatabaseDriverFactory
import com.shortspark.emaliestates.data.local.IOSDatabaseDriverFactory
import org.koin.dsl.module

actual val targetModule = module {
    single<DatabaseDriverFactory> {
        IOSDatabaseDriverFactory()
    }
}
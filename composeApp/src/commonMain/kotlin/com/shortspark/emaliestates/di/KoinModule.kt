package com.shortspark.emaliestates.di

import com.russhwolf.settings.Settings
import com.shortspark.emaliestates.MainViewModel
import com.shortspark.emaliestates.data.PropertySDK
import com.shortspark.emaliestates.data.local.DatabaseDriverFactory
import com.shortspark.emaliestates.data.local.LocalDatabase
import com.shortspark.emaliestates.data.remote.PropertyApi
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

expect val targetModule: Module

val sharedModule = module {
    single<PropertyApi> { PropertyApi() }
    single<Settings> { Settings() }

    single {
        LocalDatabase(
            databaseDriverFactory = get()
        )
    }

    single<PropertySDK> {
        PropertySDK(
            api = get(),
            database = get(),
            settings = get()
        )
    }

    viewModel { MainViewModel(sdk = get()) }
}

fun initializeKoin(
    config: (KoinApplication.() -> Unit)? = null
) {
    startKoin {
        printLogger()
        config?.invoke(this)
        modules(targetModule, sharedModule)
    }
}
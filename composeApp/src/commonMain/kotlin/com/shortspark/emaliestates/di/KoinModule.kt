package com.shortspark.emaliestates.di

import com.shortspark.emaliestates.data.repository.PropertyRepository
import com.russhwolf.settings.Settings
import com.shortspark.emaliestates.home.viewModel.MainViewModel
import com.shortspark.emaliestates.auth.viewModel.AuthViewModel
import com.shortspark.emaliestates.data.AuthSDK
import com.shortspark.emaliestates.data.PropertySDK
import com.shortspark.emaliestates.data.local.LocalDatabase
import com.shortspark.emaliestates.data.remote.AuthApi
import com.shortspark.emaliestates.data.remote.CategoryApi
import com.shortspark.emaliestates.data.remote.HttpClientFactory
import com.shortspark.emaliestates.data.remote.PropertyApi
import com.shortspark.emaliestates.data.repository.AuthRepository
import com.shortspark.emaliestates.data.repository.CategoryRepository
import com.shortspark.emaliestates.property.viewModel.PropertyDetailViewModel
import com.shortspark.emaliestates.util.SystemClock
import com.shortspark.emaliestates.home.viewModel.CategoryViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import kotlinx.datetime.Clock
import kotlin.time.ExperimentalTime
import com.sunildhiman90.kmauth.google.GoogleAuthManager // Import this
import com.sunildhiman90.kmauth.google.KMAuthGoogle

expect val targetModule: Module

@OptIn(ExperimentalTime::class)
val sharedModule = module {
    // Settings
    single<Settings> { Settings() }

    single<GoogleAuthManager> { KMAuthGoogle.googleAuthManager }

    // Clock - provide SystemClock implementation
    single<Clock> { com.shortspark.emaliestates.util.SystemClock() }

    // Database - needs to be created before AuthRepository
    single {
        LocalDatabase(
            databaseDriverFactory = get()
        )
    }

    // Auth Repository - depends on LocalDatabase
    single<AuthRepository> {
        AuthRepository(get())
    }

    // HTTP Client - depends on AuthRepository
    single {
        val factory = HttpClientFactory(
            authRepository = get()
        )
        factory.create()
    }

    // Auth API - depends on HttpClient
    single<AuthApi> {
        AuthApi(client = get())
    }

    // Auth SDK - depends on AuthApi and AuthRepository
    single<AuthSDK> {
        AuthSDK(
            api = get(),
            authRepository = get()
        )
    }

    // Auth ViewModel - depends on AuthSDK
    viewModel {
        AuthViewModel(
            sdk = get(),
            googleAuthManager = get(),
            settings = get()
        )
    }

    viewModel { (propertyId: String) ->
        PropertyDetailViewModel(
            propertyId = propertyId,
            propertySDK = get()
        )
    }

    // Property API - depends on HttpClient
    single<PropertyApi> { PropertyApi(get()) }

    // Property Repository - depends on LocalDatabase
    single<PropertyRepository> {
        PropertyRepository(get())
    }

    // Category API - depends on HttpClient
    single<CategoryApi> { CategoryApi(get()) }

    // Category Repository - depends on CategoryApi and LocalDatabase
    single<CategoryRepository> {
        CategoryRepository(
            api = get(),
            database = get()
        )
    }

    // Property SDK - depends on PropertyApi, LocalDatabase, and Settings
    single<PropertySDK> {
        PropertySDK(
            api = get(),
            repository = get(),
            settings = get(),
            clock = get()
        )
    }

    // Category ViewModel - depends on CategoryRepository
    viewModel {
        CategoryViewModel(categoryRepository = get())
    }

    // Main ViewModel - depends on PropertySDK
    viewModel {
        MainViewModel(sdk = get())
    }
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
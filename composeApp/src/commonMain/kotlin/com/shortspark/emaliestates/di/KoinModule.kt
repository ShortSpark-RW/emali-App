package com.shortspark.emaliestates.di

import com.russhwolf.settings.Settings
import com.shortspark.emaliestates.home.viewModel.MainViewModel
import com.shortspark.emaliestates.auth.viewModel.AuthViewModel
import com.shortspark.emaliestates.auth.viewModel.SignupViewModel
import com.shortspark.emaliestates.auth.viewModel.VerifyOtpViewModel
import com.shortspark.emaliestates.home.viewModel.ProfileViewModel
import com.shortspark.emaliestates.ui.screens.search.SearchViewModel
import com.shortspark.emaliestates.data.AuthSDK
import com.shortspark.emaliestates.data.PropertySDK
import com.shortspark.emaliestates.data.CategorySDK
import com.shortspark.emaliestates.data.local.LocalDatabase
import com.shortspark.emaliestates.data.remote.AuthApi
import com.shortspark.emaliestates.data.remote.CategoryApi
import com.shortspark.emaliestates.data.remote.HttpClientFactory
import com.shortspark.emaliestates.data.remote.PropertyApi
import com.shortspark.emaliestates.data.repository.AuthRepository
import com.shortspark.emaliestates.data.repository.CategoryRepository
import com.shortspark.emaliestates.data.repository.PropertyRepository
import com.shortspark.emaliestates.property.viewModel.PropertyDetailViewModel
import com.shortspark.emaliestates.home.viewModel.SplashViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import com.sunildhiman90.kmauth.google.GoogleAuthManager // Import this
import com.sunildhiman90.kmauth.google.KMAuthGoogle

expect val targetModule: Module

@OptIn(ExperimentalTime::class)
val sharedModule = module {
    // Settings
    single<Settings> { Settings() }

    single<GoogleAuthManager> { KMAuthGoogle.googleAuthManager }

    // Clock
    single<Clock> { Clock.System }

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
        HttpClientFactory(authRepository = get()).create()
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

    // Splash ViewModel
    viewModel { SplashViewModel(get()) }

    // Auth ViewModel - depends on AuthSDK
    viewModel {
        AuthViewModel(
            sdk = get(),
            googleAuthManager = get(),
        )
    }

    // Signup ViewModel - depends on AuthSDK and AuthViewModel
    viewModel {
        SignupViewModel(
            sdk = get(),
            authViewModel = get()
        )
    }

    // Verify OTP ViewModel - depends on AuthSDK and email parameter
    viewModel { (email: String) ->
        VerifyOtpViewModel(
            sdk = get(),
            email = email
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

    // Property SDK - depends on PropertyApi, LocalDatabase, and Settings
    single<PropertySDK> {
        PropertySDK(
            api = get(),
            repository = get(),
            settings = get(),
            clock = get()
        )
    }

    // Category API - depends on HttpClient
    single<CategoryApi> { CategoryApi(get()) }

    // Category Repository - depends on CategoryApi and LocalDatabase
    single<CategoryRepository> {
        CategoryRepository(
            categoryApi = get(),
            localDatabase = get()
        )
    }

    // Category SDK
    single<CategorySDK> {
        CategorySDK(
            repository = get(),
            settings = get(),
            clock = get()
        )
    }

    // Main ViewModel - depends on PropertySDK and CategorySDK
    viewModel {
        MainViewModel(
            sdk = get(),
            categorySDK = get()
        )
    }

    // Profile ViewModel - depends on AuthSDK and PropertySDK
    viewModel {
        ProfileViewModel(
            authSDK = get(),
            propertySDK = get()
        )
    }

    // Search ViewModel - depends on PropertySDK
    viewModel {
        SearchViewModel(
            get<PropertySDK>()
        )
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
package com.shortspark.emaliestates

import android.app.Application
import com.shortspark.emaliestates.di.initializeKoin
import org.koin.android.ext.koin.androidContext

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initializeKoin(
            config = { androidContext(this@MyApplication) }
        )
    }
}
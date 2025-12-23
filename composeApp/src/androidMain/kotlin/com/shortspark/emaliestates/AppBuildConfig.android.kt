package com.shortspark.emaliestates

actual object AppBuildConfig {
    actual val WEB_CLIENT_ID: String = BuildConfig.WEB_CLIENT_ID
    actual val WEB_CLIENT_SECRET: String = BuildConfig.WEB_CLIENT_SECRET
    actual val SUPABASE_URL: String = BuildConfig.SUPABASE_URL
    actual val SUPABASE_KEY: String = BuildConfig.SUPABASE_KEY

    actual val ALL_PROPERTIES_ENDPOINT: String = BuildConfig.ALL_PROPERTIES_ENDPOINT
}
package com.shortspark.emaliestates

/**
 * Defines the expectation that build-time constants will be available on each platform.
 * The common code uses this 'expect' object to access configuration values.
 */
expect object AppBuildConfig {
    val WEB_CLIENT_ID: String
    val WEB_CLIENT_SECRET: String
    val SUPABASE_URL: String
    val SUPABASE_KEY: String
    val ALL_PROPERTIES_ENDPOINT: String
}
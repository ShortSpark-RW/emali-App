package com.shortspark.emaliestates.util.helpers

import kotlinx.datetime.Clock
import kotlin.time.ExperimentalTime

/**
 * Simple platform-agnostic logger for KMP.
 * Uses expect/actual to delegate to platform-specific logging.
 */
expect class Logger(tag: String) {
    val tag: String
    fun debug(message: String, throwable: Throwable? = null)
    fun info(message: String, throwable: Throwable? = null)
    fun warning(message: String, throwable: Throwable? = null)
    fun error(message: String, throwable: Throwable? = null)
}

/**
 * Helper to create a logger for a specific class.
 */
fun loggerFor(clazz: Any): Logger = Logger(clazz::class.simpleName ?: "Unknown")

/**
 * Extension to log with class context.
 */
fun Any.logger(): Logger = loggerFor(this)

// Timestamp logging removed to avoid experimental dependencies

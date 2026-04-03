package com.shortspark.emaliestates.util.helpers

import platform.Foundation.NSLog
import platform.Foundation.currentTimeMillis

/**
 * iOS implementation of Logger using NSLog.
 */
actual class Logger actual constructor(
    actual val tag: String
) {
    private fun formatMessage(level: String, message: String, throwable: Throwable?): String {
        val timestamp = currentTimeMillis()
        val throwableMsg = throwable?.let { ": ${it.message}" } ?: ""
        return "[$timestamp] [$level] [$tag] $message$throwableMsg"
    }

    actual fun debug(message: String, throwable: Throwable?) {
        NSLog(formatMessage("DEBUG", message, throwable))
    }

    actual fun info(message: String, throwable: Throwable?) {
        NSLog(formatMessage("INFO", message, throwable))
    }

    actual fun warning(message: String, throwable: Throwable?) {
        NSLog(formatMessage("WARN", message, throwable))
    }

    actual fun error(message: String, throwable: Throwable?) {
        NSLog(formatMessage("ERROR", message, throwable))
    }
}

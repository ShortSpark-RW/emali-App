package com.shortspark.emaliestates.util.helpers

import android.util.Log

/**
 * Android implementation of Logger using android.util.Log.
 */
actual class Logger actual constructor(
    actual val tag: String
) {
    actual fun debug(message: String, throwable: Throwable?) {
        if (throwable != null) {
            Log.d(tag, message, throwable)
        } else {
            Log.d(tag, message)
        }
    }

    actual fun info(message: String, throwable: Throwable?) {
        if (throwable != null) {
            Log.i(tag, message, throwable)
        } else {
            Log.i(tag, message)
        }
    }

    actual fun warning(message: String, throwable: Throwable?) {
        if (throwable != null) {
            Log.w(tag, message, throwable)
        } else {
            Log.w(tag, message)
        }
    }

    actual fun error(message: String, throwable: Throwable?) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }
}

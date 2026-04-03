package com.shortspark.emaliestates.util

import kotlinx.datetime.Instant
import platform.Foundation.NSDate

/**
 * iOS implementation of currentTimeMillis using NSDate.
 */
actual fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()

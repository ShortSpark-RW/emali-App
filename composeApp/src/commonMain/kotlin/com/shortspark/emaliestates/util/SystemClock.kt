package com.shortspark.emaliestates.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Expect function to get current time in milliseconds since epoch.
 * Platform-specific implementations will provide the actual implementation.
 */
expect fun currentTimeMillis(): Long

/**
 * Clock implementation that uses the multiplatform currentTimeMillis expect function.
 */
class SystemClock : Clock {
    override fun now(): Instant = Instant.fromEpochMilliseconds(currentTimeMillis())
}

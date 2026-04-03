package com.shortspark.emaliestates.util

import kotlinx.datetime.Instant

/**
 * Android implementation of currentTimeMillis using System.currentTimeMillis().
 */
actual fun currentTimeMillis(): Long = System.currentTimeMillis()

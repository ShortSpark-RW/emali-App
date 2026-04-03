package com.shortspark.emaliestates.util.helpers

import kotlinx.datetime.Instant

fun String.toInstantSafe(): Instant {
    var value = this.trim()

    // SQLite -> ISO
    value = value.replace(" ", "T")

    // Remove fractional seconds if malformed
    value = value.replace(Regex("\\.\\d+"), "")

    // If no timezone, assume UTC
    if (!value.endsWith("Z") && !value.contains("+")) {
        value += "Z"
    }

    return Instant.parse(value)
}

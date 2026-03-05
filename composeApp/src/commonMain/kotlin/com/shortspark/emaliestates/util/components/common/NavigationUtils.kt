package com.shortspark.emaliestates.util.components.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * KMP-safe debounced navigator.
 * Uses kotlin.time.Clock (stdlib, available in Kotlin 2.x with kotlinx-datetime 0.7.1+)
 * instead of kotlinx.datetime.Clock which was removed in 0.7.x.
 */
@OptIn(ExperimentalTime::class)
class DebouncedNavigator(
    private val onNavigate: () -> Unit,
    private val debounceMs: Long = 500L
) {
    private var lastClickMs: Long = 0L

    fun navigate() {
        val now = Clock.System.now().toEpochMilliseconds()
        if (now - lastClickMs >= debounceMs) {
            lastClickMs = now
            onNavigate()
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun rememberDebouncedNavigator(
    debounceMs: Long = 500L,
    onNavigate: () -> Unit
): DebouncedNavigator = remember(onNavigate) {
    DebouncedNavigator(
        onNavigate = onNavigate,
        debounceMs = debounceMs
    )
}
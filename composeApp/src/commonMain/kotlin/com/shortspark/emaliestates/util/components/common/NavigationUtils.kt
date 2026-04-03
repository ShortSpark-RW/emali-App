package com.shortspark.emaliestates.util.components.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.shortspark.emaliestates.util.SystemClock
import kotlinx.datetime.Clock

/**
 * KMP-safe debounced navigator.
 * Uses kotlinx.datetime.Clock.
 */
class DebouncedNavigator(
    private val onNavigate: () -> Unit,
    private val debounceMs: Long = 500L
) {
    private var lastClickMs: Long = 0L

    fun navigate() {
        val now = SystemClock().now().toEpochMilliseconds()
        if (now - lastClickMs >= debounceMs) {
            lastClickMs = now
            onNavigate()
        }
    }
}

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
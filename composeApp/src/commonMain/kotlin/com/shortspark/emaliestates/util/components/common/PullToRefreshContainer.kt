package com.shortspark.emaliestates.util.components.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Expect composable for pull-to-refresh container.
 * Platform-specific implementations will provide actual behavior:
 * - Android: Uses Material3 PullRefreshBox
 * - iOS: Uses simple Box (or future native implementation)
 */
expect @Composable
fun PullToRefreshContainer(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
)

/**
 * Helper to remember a scroll state for the content.
 * Can be used inside the content lambda if needed.
 */
@Composable
fun rememberRefreshScrollState() = rememberScrollState()

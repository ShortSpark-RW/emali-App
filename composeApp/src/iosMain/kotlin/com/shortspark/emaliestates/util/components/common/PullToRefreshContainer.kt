package com.shortspark.emaliestates.util.components.common

import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable

/**
 * iOS actual implementation - currently just a Box wrapper.
 * Future: Could integrate native UIRefreshControl via UIKit interop.
 */
@Composable
actual fun PullToRefreshContainer(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        content()
    }
}

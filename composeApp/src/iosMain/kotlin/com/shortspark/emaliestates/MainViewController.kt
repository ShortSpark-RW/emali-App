package com.shortspark.emaliestates

import androidx.compose.ui.window.ComposeUIViewController
import com.shortspark.emaliestates.di.initializeKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initializeKoin() }
) { App() }
package com.shortspark.emaliestates.util.components.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Enum for message types with associated styling
 */
enum class MessageType {
    ERROR,
    SUCCESS,
    INFO,
    WARNING
}

/**
 * Animation style for message appearance
 */
enum class MessageAnimationStyle {
    FADE,        // Simple fade in/out
    SLIDE,       // Slide from top
    EXPAND       // Expand vertically (default)
}

/**
 * Position where the message appears
 */
enum class MessagePosition {
    TOP,         // At the top of the container
    BOTTOM,      // At the bottom (like snackbar)
    CENTER       // Center of container
}

/**
 * Enhanced animated message component that shows/hides messages with smooth transitions
 *
 * @param message The message text to display
 * @param type Message type for styling (error, success, info, warning)
 * @param isVisible Controls visibility of the message
 * @param onAutoDismiss Called when auto-dismiss timer completes (for non-error messages)
 * @param onDismiss Called when user manually dismisses the message
 * @param modifier Modifier for styling
 * @param animationStyle Animation style for entrance/exit
 * @param position Where to position the message
 * @param autoDismissDelay Delay before auto-dismiss (0 = never auto-dismiss)
 * @param showDismissButton Whether to show an X button to manually dismiss
 */
@Composable
fun AnimatedMessage(
    message: String,
    type: MessageType = MessageType.ERROR,
    isVisible: Boolean = true,
    onAutoDismiss: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    animationStyle: MessageAnimationStyle = MessageAnimationStyle.EXPAND,
    position: MessagePosition = MessagePosition.TOP,
    autoDismissDelay: Long = if (type == MessageType.ERROR) 0 else 3000,
    showDismissButton: Boolean = false
) {
    var visible by remember(isVisible) { mutableStateOf(isVisible) }

    // Auto-dismiss for non-error messages
    LaunchedEffect(isVisible, type, autoDismissDelay) {
        if (isVisible && onAutoDismiss != null && type != MessageType.ERROR && autoDismissDelay > 0) {
            delay(autoDismissDelay)
            visible = false
            onAutoDismiss()
        }
    }

    // Reset visibility when isVisible changes
    LaunchedEffect(isVisible) {
        visible = isVisible
    }

    // Determine enter/exit animations based on style
    val enterAnimation = when (animationStyle) {
        MessageAnimationStyle.FADE -> fadeIn(animationSpec = tween(durationMillis = 300))
        MessageAnimationStyle.SLIDE -> slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(durationMillis = 350)
        ) + fadeIn(animationSpec = tween(durationMillis = 300))

        MessageAnimationStyle.EXPAND -> fadeIn(animationSpec = tween(durationMillis = 300)) + expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(durationMillis = 350)
        )
    }

    val exitAnimation = when (animationStyle) {
        MessageAnimationStyle.FADE -> fadeOut(animationSpec = tween(durationMillis = 200))
        MessageAnimationStyle.SLIDE -> slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(durationMillis = 250)
        ) + fadeOut(animationSpec = tween(durationMillis = 200))
        MessageAnimationStyle.EXPAND -> fadeOut(animationSpec = tween(durationMillis = 200)) + shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween(durationMillis = 250)
        )
    }

    // Positioning container
    val positionedModifier = when (position) {
        MessagePosition.TOP -> Modifier.fillMaxWidth()
        MessagePosition.BOTTOM -> Modifier.fillMaxWidth()
        MessagePosition.CENTER -> Modifier.fillMaxWidth()
    }

    AnimatedVisibility(
        visible = visible,
        enter = enterAnimation,
        exit = exitAnimation,
        modifier = modifier.then(positionedModifier)
            .padding(horizontal = 16.dp)
            .let {
                when (position) {
                    MessagePosition.TOP -> it.padding(top = 8.dp)
                    MessagePosition.BOTTOM -> it.padding(bottom = 80.dp) // Space for bottom nav
                    MessagePosition.CENTER -> it
                }
            }
            .semantics {
                liveRegion = LiveRegionMode.Polite
                // Announce message to accessibility services
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    when (type) {
                        MessageType.ERROR -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                        MessageType.SUCCESS -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                        MessageType.INFO -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                        MessageType.WARNING -> Color(0xFFFF9800).copy(alpha = 0.15f)
                    }
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Icon
            Icon(
                imageVector = when (type) {
                    MessageType.ERROR -> Icons.Filled.Error
                    MessageType.SUCCESS -> Icons.Filled.CheckCircle
                    MessageType.INFO -> Icons.Filled.Info
                    MessageType.WARNING -> Icons.Filled.Warning
                },
                contentDescription = null,
                tint = when (type) {
                    MessageType.ERROR -> MaterialTheme.colorScheme.error
                    MessageType.SUCCESS -> Color(0xFF4CAF50)
                    MessageType.INFO -> MaterialTheme.colorScheme.secondary
                    MessageType.WARNING -> Color(0xFFFF9800)
                },
                modifier = Modifier.padding(start = 4.dp)
            )

            // Message text
            Text(
                text = message,
                color = when (type) {
                    MessageType.ERROR -> MaterialTheme.colorScheme.error
                    MessageType.SUCCESS -> Color(0xFF4CAF50)
                    MessageType.INFO -> MaterialTheme.colorScheme.onSurface
                    MessageType.WARNING -> Color(0xFFFF9800)
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            // Dismiss button (optional)
            if (showDismissButton || onDismiss != null) {
                IconButton(
                    onClick = {
                        visible = false
                        onDismiss?.invoke()
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Dismiss",
                        tint = when (type) {
                            MessageType.ERROR -> MaterialTheme.colorScheme.error
                            MessageType.SUCCESS -> Color(0xFF4CAF50)
                            MessageType.INFO -> MaterialTheme.colorScheme.secondary
                            MessageType.WARNING -> Color(0xFFFF9800)
                        },
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Convenience function to show a snackbar-like message at the top of screen
 */
@Composable
fun TopMessage(
    message: String,
    type: MessageType = MessageType.ERROR,
    isVisible: Boolean = true,
    onDismiss: (() -> Unit)? = null,
    animationStyle: MessageAnimationStyle = MessageAnimationStyle.EXPAND,
    showDismissButton: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        AnimatedMessage(
            message = message,
            type = type,
            isVisible = isVisible,
            onDismiss = onDismiss,
            animationStyle = animationStyle,
            position = MessagePosition.TOP,
            showDismissButton = showDismissButton,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

/**
 * Convenience function to show a snackbar-like message at the bottom of screen
 */
@Composable
fun BottomMessage(
    message: String,
    type: MessageType = MessageType.INFO,
    isVisible: Boolean = true,
    onDismiss: (() -> Unit)? = null,
    onAutoDismiss: (() -> Unit)? = null,
    autoDismissDelay: Long = 3000,
    animationStyle: MessageAnimationStyle = MessageAnimationStyle.SLIDE,
    showDismissButton: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 80.dp), // Above bottom navigation
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedMessage(
            message = message,
            type = type,
            isVisible = isVisible,
            onDismiss = onDismiss,
            onAutoDismiss = onAutoDismiss,
            autoDismissDelay = autoDismissDelay,
            animationStyle = animationStyle,
            position = MessagePosition.BOTTOM,
            showDismissButton = showDismissButton
        )
    }
}

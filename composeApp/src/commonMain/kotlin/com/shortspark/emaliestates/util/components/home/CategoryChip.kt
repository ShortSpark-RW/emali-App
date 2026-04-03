package com.shortspark.emaliestates.util.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shortspark.emaliestates.domain.Category

/**
 * A chip component for displaying a category.
 * Shows an optional icon and the category name.
 *
 * @param category The category to display
 * @param isSelected Whether this chip is currently selected (highlighted)
 * @param onClick Callback when the chip is clicked
 * @param modifier Modifier for customizing layout
 */
@Composable
fun CategoryChip(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.surface
    }

    val textColor = if (isSelected) {
        Color.White
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.outline
    }

    val contentDescription = "Category: ${category.name}" + if (isSelected) " selected" else ""

    Box(
        modifier = modifier
            .semantics { this.contentDescription = contentDescription }
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Optional icon (if provided)
            category.icon?.let { iconUrl ->
                // Placeholder for icon - could be a painter or vector
                // For now, we'll skip as we don't have vector data
                // Icon(painter = rememberImagePainter(iconUrl), contentDescription = null, modifier = Modifier.size(16.dp))
            }

            if (category.icon != null) {
                Spacer(modifier = Modifier.width(6.dp))
            }

            Text(
                text = category.name,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Full-width CategoryChip that takes available width.
 * Used in horizontal scroll lists.
 */
@Composable
fun CategoryChipFull(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CategoryChip(
        category = category,
        isSelected = isSelected,
        onClick = onClick,
        modifier = modifier
            .padding(horizontal = 4.dp)
            .height(40.dp)
    )
}

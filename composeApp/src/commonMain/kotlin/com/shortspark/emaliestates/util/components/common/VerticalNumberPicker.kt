package com.shortspark.emaliestates.util.components.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun VerticalNumberPicker(
    values: List<Int>,
    selected: Int,
    onSelect: (Int) -> Unit
) {
    val itemCount = values.size
    val virtualCount = Int.MAX_VALUE
    val middleIndex = virtualCount / 2 - (virtualCount / 2 % itemCount)

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = middleIndex
    )

    val coroutineScope = rememberCoroutineScope()

    val centeredIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val viewportCenter =
                (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2

            layoutInfo.visibleItemsInfo.minByOrNull { item ->
                kotlin.math.abs(
                    (item.offset + item.size / 2) - viewportCenter
                )
            }?.index
        }
    }

    LaunchedEffect(centeredIndex) {
        centeredIndex?.let { index ->
            val value = values[index % itemCount]
            if (value != selected) onSelect(value)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.height(90.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(virtualCount) { virtualIndex ->
            val value = values[virtualIndex % itemCount]
            val isSelected = value == selected

            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0.65f,
                label = "scale"
            )

            val alpha by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0.55f,
                label = "alpha"
            )

            Text(
                text = value.toString().padStart(2, '0'),
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
                    .clickable {
                        coroutineScope.launch {
                            listState.animateScrollToItem(virtualIndex)
                        }
                    },
                color = if (isSelected)
                    MaterialTheme.colorScheme.secondary
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}


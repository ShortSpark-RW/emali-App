package com.shortspark.emaliestates.util.components.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shortspark.emaliestates.util.components.common.InputLabel
import com.shortspark.emaliestates.util.components.common.VerticalNumberPicker

@Composable
fun LabeledVerticalPicker(
    label: String,
    values: List<Int>,
    selected: Int,
    onSelect: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InputLabel(text = label, fontSize = 8.sp)
        Spacer(modifier = Modifier.height(4.dp))
        VerticalNumberPicker(
            values = values,
            selected = selected,
            onSelect = onSelect,
        )
    }
}
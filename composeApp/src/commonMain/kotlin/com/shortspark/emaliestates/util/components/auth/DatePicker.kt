package com.shortspark.emaliestates.util.components.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shortspark.emaliestates.util.components.common.InputLabel
import com.shortspark.emaliestates.util.components.common.VerticalNumberPicker

@Composable
fun DatePicker(
    day: Int,
    month: Int,
    year: Int,
    onDayChange: (Int) -> Unit,
    onMonthChange: (Int) -> Unit,
    onYearChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        InputLabel("Date of Birth")
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            LabeledVerticalPicker(
                values = (1..31).toList(),
                selected = day,
                onSelect = onDayChange,
                label = "DD"
            )
            LabeledVerticalPicker(
                values = (1..12).toList(),
                selected = month,
                onSelect = onMonthChange,
                label = "MM"
            )
            LabeledVerticalPicker(
                values = (1950..2007).toList(),
                selected = year,
                onSelect = onYearChange,
                label = "YYYY"
            )
        }
    }
}
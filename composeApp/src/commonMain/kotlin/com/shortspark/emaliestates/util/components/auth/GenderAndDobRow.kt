package com.shortspark.emaliestates.util.components.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview(showBackground = true)
fun GenderAndDobRow(
    gender: Gender,
    onGenderChange: (Gender) -> Unit,

    day: Int,
    month: Int,
    year: Int,
    onDayChange: (Int) -> Unit,
    onMonthChange: (Int) -> Unit,
    onYearChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        GenderSelector(
            selected = gender,
            onSelected = onGenderChange,
        )

        DatePicker(
            day = day,
            month = month,
            year = year,
            onDayChange = onDayChange,
            onMonthChange = onMonthChange,
            onYearChange = onYearChange,
        )
    }
}
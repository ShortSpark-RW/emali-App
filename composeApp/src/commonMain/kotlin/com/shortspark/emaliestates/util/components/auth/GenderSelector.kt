package com.shortspark.emaliestates.util.components.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shortspark.emaliestates.util.components.common.InputLabel

@Composable
fun GenderSelector(
    selected: Gender,
    onSelected: (Gender) -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        InputLabel(text = "Gender")
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier,
        ) {
            GenderOption(
                label = "Male",
                selected = selected == Gender.MALE,
                onClick = { onSelected(Gender.MALE) }
            )
            GenderOption(
                label = "Female",
                selected = selected == Gender.FEMALE,
                onClick = { onSelected(Gender.FEMALE) }
            )
        }
    }
}

@Composable
private fun GenderOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.secondary,
                unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            ),
            modifier = Modifier.size(24.dp).padding(end = 8.dp)
        )
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

enum class Gender { MALE, FEMALE }
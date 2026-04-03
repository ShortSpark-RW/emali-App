package com.shortspark.emaliestates.util.components.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shortspark.emaliestates.domain.Countries
import com.shortspark.emaliestates.domain.Country
import com.shortspark.emaliestates.util.components.common.CommonOutlinedTextField

@Composable
fun CountryPickerDialog(
    countries: List<Country>,
    onSelect: (Country) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = { Text("Select country") },
        text = {
            LazyColumn {
                items(countries) { country ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelect(country)
                                onDismiss()
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(country.flagEmoji, fontSize = 22.sp)
                        Spacer(Modifier.width(12.dp))
                        Text("${country.name} (${country.dialCode})")
                    }
                }
            }
        }
    )
}


@Composable
fun PhoneNumberOutlinedTextField(
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,

    selectedCountry: Country,
    onCountrySelected: (Country) -> Unit,

    isFocused: Boolean,
    onFocusChange: (FocusState) -> Unit,

    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    var showPicker by remember { mutableStateOf(false) }

    if (showPicker) {
        CountryPickerDialog(
            countries = Countries,
            onSelect = onCountrySelected,
            onDismiss = { showPicker = false }
        )
    }

    CommonOutlinedTextField(
        value = phoneNumber,
        onValueChange = {
            if (it.all { char -> char.isDigit() }) {
                onPhoneNumberChange(it)
            }
        },
        isFocused = isFocused,
        onFocusChange = onFocusChange,
        label = "Phone number",
        placeholder = "7XXXXXXXX",
        keyboardType = KeyboardType.Phone,
        imeAction = ImeAction.Done,
        isError = isError,
        errorMessage = errorMessage,
        leadingIcon = {
            TextButton(
                onClick = { showPicker = true },
                contentPadding = PaddingValues(start = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select country",
                    tint = if (isFocused) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "${selectedCountry.flagEmoji} ${selectedCountry.dialCode}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        modifier = modifier
    )
}

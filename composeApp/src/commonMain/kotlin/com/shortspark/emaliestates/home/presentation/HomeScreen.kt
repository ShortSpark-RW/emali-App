package com.shortspark.emaliestates.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shortspark.emaliestates.MainViewModel
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.domain.RequestState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen() {

    val viewModel = koinViewModel<MainViewModel>()
    val allProperties by viewModel.allProperties

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Use a when expression to handle the different states of RequestState
        when (val state = allProperties) {
            is RequestState.Success -> {
                // Safely get the list of properties from the Success state
                val data = state.data

                // Check if the list is empty before trying to display
                if (data.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(), // Make LazyColumn fill the available space
                        verticalArrangement = Arrangement.spacedBy(12.dp) // Add spacing between items
                    ) {
                        // Use the 'items' overload that takes a list and a lambda
                        items(
                            items = data,
                            key = { it.id } // 'it' is fine here as it's a single parameter lambda
                        ) { property -> // Use a descriptive name like 'property'
                            // Spacer is now placed *inside* the item content
                            Spacer(modifier = Modifier.height(12.dp)) // Reduced spacing slightly for better look
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                                text = "${property.id} - ${property.title}",
                                // Use style for cleaner typography application
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                } else {
                    // Handle the case where data is available but empty
                    Text("No properties found.")
                }
            }
            is RequestState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Error: ${state.message}", // Display error message
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            is RequestState.Idle, is RequestState.Loading -> { // Handle Idle and Loading states
                CircularProgressIndicator()
            }
        }
    }

}

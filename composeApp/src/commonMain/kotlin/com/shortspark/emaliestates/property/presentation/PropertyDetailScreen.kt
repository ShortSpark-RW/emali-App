package com.shortspark.emaliestates.property.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.domain.RequestState
import com.shortspark.emaliestates.property.viewModel.PropertyDetailViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

// ─── Screen Entry Point ───────────────────────────────────────────────────────

@Composable
fun PropertyDetailScreen(
    propertyId: String,
    navController: NavController
) {
    val viewModel = koinViewModel<PropertyDetailViewModel> { parametersOf(propertyId) }
    val propertyState by viewModel.propertyState.collectAsState()
    val similarProperties by viewModel.similarProperties.collectAsState()

    PropertyDetailContent(
        propertyState = propertyState,
        similarProperties = similarProperties,
        onBack = { navController.popBackStack() },
        onScheduleTour = { viewModel.scheduleTour() },
        onSimilarPropertyClick = { id -> navController.navigate("property/$id") }
    )
}

// ─── Content ──────────────────────────────────────────────────────────────────

@Composable
fun PropertyDetailContent(
    propertyState: RequestState<Property?> = RequestState.Idle,
    similarProperties: List<Property> = emptyList(),
    onBack: () -> Unit = {},
    onScheduleTour: () -> Unit = {},
    onSimilarPropertyClick: (String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (propertyState) {
            is RequestState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            is RequestState.Error -> {
                Text(
                    text = propertyState.message.toString(),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                )
            }

            is RequestState.Success -> {
                val property = propertyState.data
                if (property != null) {
                    PropertyDetailBody(
                        property = property,
                        similarProperties = similarProperties,
                        onBack = onBack,
                        onScheduleTour = onScheduleTour,
                        onSimilarPropertyClick = onSimilarPropertyClick
                    )
                } else {
                    Text(
                        text = "Property not found.",
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            else -> Unit
        }
    }
}

// ─── Detail Body ─────────────────────────────────────────────────────────────

@Composable
fun PropertyDetailBody(
    property: Property,
    similarProperties: List<Property>,
    onBack: () -> Unit,
    onScheduleTour: () -> Unit,
    onSimilarPropertyClick: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    var isFav by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // ── Hero ──────────────────────────────────────────────────────────
            PropertyHeroSection(
                imageUrl = property.featuredImg,
                additionalImages = property.additionalImgs,
                isFav = isFav,
                onFavToggle = { isFav = !isFav },
                onBack = onBack,
                onShare = {}
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(Modifier.height(16.dp))

                // ── Title + Price ─────────────────────────────────────────────
                PropertyTitleRow(
                    title = property.title,
                    location = property.placeName ?: property.locationId ?: "",
                    rating = 4.5f,
                    price = property.price,
                    saleType = property.saleType.name
                )

                PropertyDivider()

                // ── Specs ─────────────────────────────────────────────────────
                PropertyDetailsSection(property = property)

                PropertyDivider()

                // ── Description ───────────────────────────────────────────────
                PropertyDescriptionSection(description = property.description ?: "")

                // ── Amenities (if any) ────────────────────────────────────────
                if (property.amenities.isNotEmpty()) {
                    PropertyDivider()
                    PropertyAmenitiesSection(amenities = property.amenities)
                }

                PropertyDivider()

                // ── Similar Properties ────────────────────────────────────────
                if (similarProperties.isNotEmpty()) {
                    SimilarPropertiesSection(
                        properties = similarProperties,
                        onPropertyClick = onSimilarPropertyClick
                    )
                    PropertyDivider()
                }

                // ── Listing Agent ─────────────────────────────────────────────
                ListingAgentSection(
                    agentName = "Muhire Kevin",
                    agentTitle = "Real Estate Specialist",
                    onCall = {},
                    onMessage = {}
                )

                PropertyDivider()

                // ── Location Map ──────────────────────────────────────────────
                PropertyLocationSection()

                Spacer(Modifier.height(100.dp)) // clearance for FAB
            }
        }

        // ── Floating CTA ──────────────────────────────────────────────────────
        ScheduleTourButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            onClick = onScheduleTour
        )
    }
}

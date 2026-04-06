package com.shortspark.emaliestates.property.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.domain.SaleType
import com.shortspark.emaliestates.util.components.common.rememberDebouncedNavigator

// ─── Hero Image Section ───────────────────────────────────────────────────────

@Composable
fun PropertyHeroSection(
    imageUrl: String?,
    additionalImages: List<String>,
    isFav: Boolean,
    onFavToggle: () -> Unit,
    onBack: () -> Unit,
    onShare: () -> Unit
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val allImages = buildList {
        if (!imageUrl.isNullOrBlank()) add(imageUrl)
        addAll(additionalImages)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        // Main image
        if (allImages.isNotEmpty()) {
            AsyncImage(
                model = allImages[selectedIndex],
                contentDescription = "Property image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }

        // Gradient scrim so top buttons are always readable
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.55f), Color.Transparent)
                    )
                )
        )

        // Back button
        HeroIconButton(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart),
            onClick = onBack
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        // Fav + Share buttons
        Row(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopEnd),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HeroIconButton(onClick = onFavToggle) {
                Icon(
                    imageVector = if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favourite",
                    tint = if (isFav) MaterialTheme.colorScheme.secondary else Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            HeroIconButton(onClick = onShare) {
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = "Share",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Image pagination dots
        if (allImages.size > 1) {
            ImagePagerDots(
                total = allImages.size,
                selected = selectedIndex,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
            )
        }
    }

    // Thumbnail strip (if multiple images)
    if (allImages.size > 1) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            allImages.forEachIndexed { index, url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = if (index == selectedIndex) 2.dp else 0.dp,
                            color = if (index == selectedIndex) MaterialTheme.colorScheme.secondary else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedIndex = index }
                )
            }
        }
    }
}

@Composable
fun HeroIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.35f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun ImagePagerDots(total: Int, selected: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(total) { index ->
            Box(
                modifier = Modifier
                    .size(if (index == selected) 10.dp else 7.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == selected) MaterialTheme.colorScheme.secondary
                        else Color.White.copy(alpha = 0.5f)
                    )
            )
        }
    }
}

// ─── Title Row ────────────────────────────────────────────────────────────────

@Composable
fun PropertyTitleRow(
    title: String,
    location: String,
    rating: Float,
    price: Float,
    saleType: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📍", fontSize = 12.sp)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = location,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 13.sp
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⭐", fontSize = 13.sp)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = rating.toString(),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )) {
                        append("$${formatPrice(price)}")
                    }
                    if (saleType.equals("rent", ignoreCase = true)) {
                        withStyle(SpanStyle(
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 13.sp
                        )) {
                            append("/month")
                        }
                    }
                }
            )
        }
    }
}

// ─── Property Details Section ─────────────────────────────────────────────────

@Composable
fun PropertyDetailsSection(property: Property) {
    Column {
        Text(
            text = "Property Details",
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PropertyDetailItem(icon = "🛏", value = "${property.bedrooms}", label = "Bedroom")
            PropertyDetailItem(icon = "🚿", value = "${property.bathrooms}", label = "Bathrooms")
            PropertyDetailItem(icon = "📐", value = "${property.area.toInt()}", label = "Area in sqft")
            PropertyDetailItem(icon = "🚗", value = "2", label = "Parking") // TODO: wire when domain has parking field
        }
    }
}

@Composable
fun PropertyDetailItem(icon: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icon, fontSize = 22.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            fontSize = 11.sp
        )
    }
}

// ─── Description Section ──────────────────────────────────────────────────────

@Composable
fun PropertyDescriptionSection(description: String) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Property Description",
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = description,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
            fontSize = 13.sp,
            lineHeight = 20.sp,
            maxLines = if (expanded) Int.MAX_VALUE else 4
        )
        if (description.length > 200) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = if (expanded) "Show less" else "Read more",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { expanded = !expanded }
            )
        }
    }
}

// ─── Amenities Section ────────────────────────────────────────────────────────

@Composable
fun PropertyAmenitiesSection(amenities: List<String>) {
    if (amenities.isEmpty()) return

    Column {
        Text(
            text = "Amenities",
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            amenities.forEach { amenity ->
                AmenityChip(label = amenity)
            }
        }
    }
}

@Composable
fun AmenityChip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp
        )
    }
}

// ─── Location Section ─────────────────────────────────────────────────────────

@Composable
fun PropertyLocationSection() {
    Column {
        Text(
            text = "Location",
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(12.dp))
        // Map placeholder — replace with actual map composable (e.g. Google Maps SDK)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📍 Map View",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                fontSize = 14.sp
            )
        }
    }
}

// ─── Similar Properties Section ───────────────────────────────────────────────

@Composable
fun SimilarPropertiesSection(
    properties: List<Property>,
    onPropertyClick: (String) -> Unit
) {
    if (properties.isEmpty()) return

    Column {
        Text(
            text = "Similar properties",
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            properties.forEach { property ->
                PropertyCard(
                    property = property,
                    onFavoriteToggle = {},
//                    onCardClick = {  }
                )
            }
        }
    }
}

// ─── Listing Agent Section ────────────────────────────────────────────────────

@Composable
fun ListingAgentSection(
    agentName: String = "Muhire Kevin",
    agentTitle: String = "Real Estate Specialist",
    onCall: () -> Unit = {},
    onMessage: () -> Unit = {}
) {
    Column {
        Text(
            text = "Listing Agent",
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Agent avatar placeholder
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👤", fontSize = 24.sp)
                }
                Column {
                    Text(
                        text = agentName,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = agentTitle,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AgentActionButton(icon = "📞", onClick = onCall)
                AgentActionButton(icon = "💬", onClick = onMessage)
            }
        }
    }
}

@Composable
fun AgentActionButton(icon: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = icon, fontSize = 18.sp)
    }
}

// ─── Schedule Tour Button ─────────────────────────────────────────────────────

@Composable
fun ScheduleTourButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(30.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 8.dp)
    ) {
        Text(
            text = "Schedule a Tour",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

// ─── Shared Divider ───────────────────────────────────────────────────────────

@Composable
fun PropertyDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 16.dp),
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f)
    )
}

// ─── Reusable PropertyCard (detail page variant with click) ───────────────────

@Composable
fun PropertyCard(
    property: Property,
    onFavoriteToggle: (String) -> Unit = {},
    onCardClick: () -> Unit = {}
) {
    var isFav by remember { mutableStateOf(false) }

    val debouncedNavigator = rememberDebouncedNavigator {
        onCardClick()
    }

    Column(
        modifier = Modifier
            .width(180.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable (
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                debouncedNavigator.navigate()
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (!property.featuredImg.isNullOrBlank()) {
                AsyncImage(
                    model = property.featuredImg,
                    contentDescription = property.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomStart)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.55f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = property.type.name.lowercase().replaceFirstChar { it.uppercase() },
                    color = Color.White,
                    fontSize = 11.sp
                )
            }
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopEnd)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (isFav) MaterialTheme.colorScheme.secondary else Color.Black.copy(alpha = 0.35f))
                    .clickable {
                        isFav = !isFav
                        onFavoriteToggle(property.id)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = if (isFav) "♥" else "♡", fontSize = 13.sp, color = Color.White)
            }
        }

        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = property.title,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                maxLines = 1
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📍", fontSize = 10.sp)
                Spacer(Modifier.width(2.dp))
                Text(
                    text = property.locationId ?: "",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            }
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$${formatPrice(property.price)}",
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                if (property.saleType == SaleType.RENT) {
                    Text(text = "/mo", color = MaterialTheme.colorScheme.secondary, fontSize = 11.sp)
                }
            }
        }
    }
}

// ─── Utility ──────────────────────────────────────────────────────────────────

fun formatPrice(price: Float): String = when {
    price >= 1_000_000 -> "${(price / 1_000_000 * 10).toInt() / 10.0}M"
    price >= 1_000     -> "${(price / 1_000).toInt()}K"
    else               -> "${price.toInt()}"
}
package com.shortspark.emaliestates.home.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.home.viewModel.ProfileViewModel
import com.shortspark.emaliestates.navigation.BaseScreen
import org.koin.compose.viewmodel.koinViewModel

// ─── Theme Colors (using MaterialTheme) ───────────────────────────────────────

@Composable
private fun Orange(): Color = MaterialTheme.colorScheme.secondary

@Composable
private fun Background(): Color = MaterialTheme.colorScheme.background

@Composable
private fun Surface(): Color = MaterialTheme.colorScheme.surface

@Composable
private fun SurfaceAlt(): Color = MaterialTheme.colorScheme.surfaceVariant

@Composable
private fun TextPrimary(): Color = MaterialTheme.colorScheme.onBackground

@Composable
private fun TextSecondary(): Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

@Composable
private fun Divider(): Color = MaterialTheme.colorScheme.outline

// ─── Data Models ─────────────────────────────────────────────────────────────

data class AgentProfile(
    val name: String,
    val role: String,
    val avatarUrl: String?,
    val posts: Int,
    val followers: Int,
    val dealsClosed: Int,
    val bio: String
)

private enum class ProfileTab(val icon: ImageVector, val contentDesc: String) {
    POSTS(Icons.Default.GridView, "Posts"),
    VIDEOS(Icons.Default.PlayCircleOutline, "Videos"),
    FAVORITES(Icons.Default.FavoriteBorder, "Favorites"),
    DEALS(Icons.Default.Handshake, "Deals Closed")
}

// ─── Profile Screen ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.profileState.collectAsState()
    val currentUser = viewModel.currentUser

    // Refresh profile when this screen becomes visible
    LaunchedEffect(Unit) {
        // This will be called when the screen enters the composition
        viewModel.loadProfile()
    }

    // Also refresh when navigating back to this screen
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, _, _ ->
            viewModel.loadProfile()
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

    // Determine if this is the user's own profile
    val isOwnProfile = currentUser != null

    // Build agent profile from user data (for now using sample/mock data)
    val agentProfile = remember(currentUser) {
        AgentProfile(
            name = currentUser?.name ?: "Guest User",
            role = if (isOwnProfile) "Property Owner" else "Agent",
            avatarUrl = null, // TODO: Add avatar URL to User model or fetch from API
            posts = viewModel.totalProperties,
            followers = 0, // TODO: Implement followers feature
            dealsClosed = viewModel.soldProperties,
            bio = "Welcome to my profile! I'm a real estate professional dedicated to helping you find the perfect property."
        )
    }

    var selectedTab by remember { mutableStateOf(ProfileTab.POSTS) }
    var showContactSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background())
            .windowInsetsPadding(WindowInsets.systemBars)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Top nav bar ───────────────────────────────────────────────────
        ProfileTopBar(
            navController = navController,
            isOwnProfile = isOwnProfile
        )

        // ── Avatar + name + stats ─────────────────────────────────────────
        ProfileHeader(agent = agentProfile)

        Spacer(Modifier.height(16.dp))

        // ── Action buttons ────────────────────────────────────────────────
        ProfileActionButtons(
            isOwnProfile = isOwnProfile,
            onEditProfile = {
                navController.navigate(BaseScreen.EditProfile.route)
            },
            onShareProfile = {
                // TODO: Implement share functionality
            },
            onContact = { showContactSheet = true }
        )

        Spacer(Modifier.height(20.dp))

        // ── Content tab icons ─────────────────────────────────────────────
        ProfileTabRow(
            selected = selectedTab,
            onSelect = { selectedTab = it }
        )

        Spacer(Modifier.height(16.dp))

        // ── 2-column property grid ────────────────────────────────────────
        val filteredPosts = when (selectedTab) {
            ProfileTab.POSTS -> viewModel.properties
            ProfileTab.DEALS -> viewModel.properties.filter { it.isSold || it.isRented }
            ProfileTab.VIDEOS -> viewModel.properties.filter { !it.videoUrl.isNullOrEmpty() }
            ProfileTab.FAVORITES -> emptyList() // TODO: Implement favorites
        }

        PropertyGrid(
            posts = filteredPosts,
            activeTab = selectedTab,
            navController = navController
        )

        Spacer(Modifier.height(20.dp))
    }

    // ── Contact bottom sheet ────────────────────────────────────────────────
    if (showContactSheet) {
        ContactBottomSheet(
            agentEmail = currentUser?.email ?: "",
            sheetState = sheetState,
            onDismiss = { showContactSheet = false }
        )
    }
}

// ─── Top Bar ──────────────────────────────────────────────────────────────────

@Composable
private fun ProfileTopBar(
    navController: NavController,
    isOwnProfile: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextPrimary(),
                modifier = Modifier.size(22.dp)
            )
        }

        if (isOwnProfile) {
            IconButton(onClick = { navController.navigate(BaseScreen.EditProfile.route) }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Edit Profile",
                    tint = TextPrimary(),
                    modifier = Modifier.size(22.dp)
                )
            }
        } else {
            Spacer(Modifier.size(48.dp))
        }
    }
}

// ─── Profile Header (avatar + name + stats + bio) ────────────────────────────

@Composable
private fun ProfileHeader(agent: AgentProfile) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Orange ring avatar
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .border(3.dp, Orange(), CircleShape)
                .padding(3.dp)
        ) {
            if (agent.avatarUrl != null) {
                AsyncImage(
                    model = agent.avatarUrl,
                    contentDescription = agent.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            } else {
                // Placeholder avatar
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Surface()),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = TextSecondary(),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = agent.name,
            color = TextPrimary(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(2.dp))

        Text(
            text = agent.role,
            color = TextSecondary(),
            fontSize = 13.sp
        )

        Spacer(Modifier.height(16.dp))

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(value = agent.posts.toString(), label = "Post", highlight = false)
            StatItem(value = agent.followers.toString(), label = "Followers", highlight = false)
            StatItem(value = agent.dealsClosed.toString(), label = "Deals Closed", highlight = true)
        }

        Spacer(Modifier.height(16.dp))

        // Bio
        Text(
            text = agent.bio,
            color = TextSecondary(),
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun StatItem(value: String, label: String, highlight: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = if (highlight) Orange() else TextPrimary(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = TextSecondary(),
            fontSize = 12.sp
        )
    }
}

// ─── Action Buttons ───────────────────────────────────────────────────────────

@Composable
private fun ProfileActionButtons(
    isOwnProfile: Boolean,
    onEditProfile: () -> Unit,
    onShareProfile: () -> Unit,
    onContact: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Edit / Follow — primary filled
        Button(
            onClick = onEditProfile,
            modifier = Modifier
                .weight(1f)
                .height(40.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Orange())
        ) {
            Text(
                text = if (isOwnProfile) "Edit Profile" else "Follow",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Share Profile — outlined
        OutlinedButton(
            onClick = onShareProfile,
            modifier = Modifier
                .weight(1f)
                .height(40.dp),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Text(
                text = "Share",
                color = TextPrimary(),
                fontSize = 13.sp
            )
        }

        // Contact — outlined (only for other profiles)
        if (!isOwnProfile) {
            OutlinedButton(
                onClick = onContact,
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                shape = RoundedCornerShape(10.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.horizontalGradient(listOf(SurfaceAlt(), SurfaceAlt()))
                )
            ) {
                Text(
                    text = "Contact",
                    color = TextPrimary(),
                    fontSize = 13.sp
                )
            }
        }
    }
}

// ─── Profile Tab Row ──────────────────────────────────────────────────────────

@Composable
private fun ProfileTabRow(
    selected: ProfileTab,
    onSelect: (ProfileTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ProfileTab.entries.forEach { tab ->
            val isSelected = tab == selected
            val iconColor by animateColorAsState(
                targetValue = if (isSelected) TextPrimary() else TextSecondary(),
                animationSpec = tween(200),
                label = "tab_icon_color"
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onSelect(tab) }
                    )
                    .padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = tab.icon,
                    contentDescription = tab.contentDesc,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.height(6.dp))
                // underline
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .width(if (isSelected) 28.dp else 0.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(if (isSelected) TextPrimary() else Color.Transparent)
                )
            }
        }
    }

    HorizontalDivider(color = Divider(), thickness = 1.dp)
}

// ─── Property Grid ────────────────────────────────────────────────────────────

@Composable
private fun PropertyGrid(
    posts: List<Property>,
    activeTab: ProfileTab,
    navController: NavController? = null
) {
    val chunked = posts.chunked(2)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        chunked.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                row.forEach { post ->
                    PropertyGridCard(
                        post = post,
                        activeTab = activeTab,
                        modifier = Modifier.weight(1f),
                        navController = navController
                    )
                }
                // fill empty slot if row has 1 item
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun PropertyGridCard(
    post: Property,
    activeTab: ProfileTab,
    modifier: Modifier = Modifier,
    navController: NavController? = null
) {
    var favorited by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Surface())
            .clickable {
                navController?.navigate(BaseScreen.PropertyDetail.createRoute(post.id))
            }
    ) {
        // Image area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            if (post.featuredImg != null) {
                AsyncImage(
                    model = post.featuredImg,
                    contentDescription = post.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Placeholder
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SurfaceAlt()),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = TextSecondary(),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0x44000000), Color(0xBB000000))
                        )
                    )
            )

            // Top-left: views count (placeholder)
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0x88000000))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Visibility, null, tint = Color.White, modifier = Modifier.size(10.dp))
                    Spacer(Modifier.width(2.dp))
                    Text("0", color = Color.White, fontSize = 9.sp)
                }
            }

            // Top-right: favorite OR more options based on tab
            when (activeTab) {
                ProfileTab.POSTS, ProfileTab.DEALS -> {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0x88000000))
                            .clickable { favorited = !favorited },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (favorited) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = if (favorited) Orange() else Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                ProfileTab.VIDEOS -> {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0x88000000))
                            .clickable {},
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.MoreHoriz, null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                ProfileTab.FAVORITES -> {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0x88000000))
                            .clickable {},
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Favorite, null,
                            tint = Orange(),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Sold badge for deals
            if (post.isSold && activeTab == ProfileTab.DEALS) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 34.dp, end = 6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF4CAF50))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("Sold", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Video play button overlay
            if (activeTab == ProfileTab.VIDEOS ||
                (activeTab == ProfileTab.FAVORITES && !post.videoUrl.isNullOrEmpty())) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x99000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PlayArrow, null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Bottom badge (property type)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0x88000000))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(post.type.name, color = Color.White, fontSize = 9.sp)
            }

            // Videos tab: more options at bottom right
            if (activeTab == ProfileTab.VIDEOS) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                ) {
                    Icon(Icons.Default.MoreHoriz, null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
        }

        // Card info
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = post.title,
                color = TextPrimary(),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(3.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Orange(), modifier = Modifier.size(11.dp))
                    Spacer(Modifier.width(2.dp))
                    Text("0.0", color = TextSecondary(), fontSize = 10.sp) // TODO: Add rating to Property
                }
                Text(
                    text = when {
                        post.price < 1_000_000 -> "$${post.price.toInt()}"
                        else -> "$${(post.price / 1_000_000).toInt()}M"
                    },
                    color = Orange(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = Orange(), modifier = Modifier.size(11.dp))
                Spacer(Modifier.width(2.dp))
                Text(
                    text = post.placeName ?: post.locationId ?: "Unknown",
                    color = TextSecondary(),
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ─── Contact Bottom Sheet ─────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactBottomSheet(
    agentEmail: String,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Surface(),
        dragHandle = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(SurfaceAlt())
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "Contact",
                    color = TextPrimary(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp)
        ) {
            // Call row
            ContactRow(
                label = "Call",
                value = null,
                onClick = {}
            )
            HorizontalDivider(color = Divider())

            // Email row
            ContactRow(
                label = "Email",
                value = agentEmail,
                onClick = {}
            )
            HorizontalDivider(color = Divider())
        }
    }
}

@Composable
private fun ContactRow(
    label: String,
    value: String?,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp)
    ) {
        Text(text = label, color = TextPrimary(), fontSize = 14.sp, fontWeight = FontWeight.Medium)
        if (value != null) {
            Spacer(Modifier.height(2.dp))
            Text(text = value, color = TextSecondary(), fontSize = 13.sp)
        }
    }
}

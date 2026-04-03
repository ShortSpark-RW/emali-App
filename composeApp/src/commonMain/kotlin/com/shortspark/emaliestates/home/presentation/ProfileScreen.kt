package com.shortspark.emaliestates.home.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.shortspark.emaliestates.auth.viewModel.AuthViewModel
import com.shortspark.emaliestates.domain.auth.User
import com.shortspark.emaliestates.navigation.Graph
import com.shortspark.emaliestates.theme.EmaliEstatesTheme
import com.shortspark.emaliestates.util.components.common.AppButton
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController
) {
    val authViewModel = koinViewModel<AuthViewModel>()
    val currentUser by authViewModel.currentUser.collectAsState()

    var expandedSection by remember { mutableStateOf<ProfileSection?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Header Card
            ProfileHeaderCard(
                user = currentUser,
                onEditProfile = { /* TODO: Navigate to edit profile */ }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Account Settings Section
            ExpandableSection(
                title = "Account Settings",
                icon = Icons.Default.Settings,
                expanded = expandedSection == ProfileSection.ACCOUNT,
                onToggle = {
                    expandedSection = if (expandedSection == ProfileSection.ACCOUNT) null else ProfileSection.ACCOUNT
                }
            ) {
                ProfileMenuItem(
                    icon = Icons.Filled.Person,
                    title = "Edit Profile",
                    subtitle = "Update your personal information",
                    onClick = { /* TODO: Edit profile */ }
                )
                ProfileMenuItem(
                    icon = Icons.Filled.Lock,
                    title = "Change Password",
                    subtitle = "Update your password",
                    onClick = { /* TODO: Change password */ }
                )
                ProfileMenuItem(
                    icon = Icons.Default.Email,
                    title = "Email Preferences",
                    subtitle = "Manage email notifications",
                    onClick = { /* TODO: Email preferences */ }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Personal Information Section
            ExpandableSection(
                title = "Personal Information",
                icon = Icons.Filled.Info,
                expanded = expandedSection == ProfileSection.PERSONAL,
                onToggle = {
                    expandedSection = if (expandedSection == ProfileSection.PERSONAL) null else ProfileSection.PERSONAL
                }
            ) {
                val user = currentUser
                if (user != null) {
                    InfoRow(icon = Icons.Default.AccountCircle, label = "Username", value = user.username ?: "Not set")
                    InfoRow(icon = Icons.Default.Badge, label = "Role", value = user.role)
                    if (user.phone?.isNotBlank() == true) {
                        InfoRow(icon = Icons.Default.Phone, label = "Phone", value = user.phone)
                    }
                    if (user.dob?.isNotBlank() == true) {
                        InfoRow(icon = Icons.Default.Cake, label = "Date of Birth", value = user.dob)
                    }
                    if (user.address?.isNotBlank() == true) {
                        InfoRow(icon = Icons.Default.LocationOn, label = "Address", value = user.address)
                    }
                    if (user.country?.isNotBlank() == true) {
                        InfoRow(icon = Icons.Default.LocationOn, label = "Country", value = user.country)
                    }
                    if (user.province?.isNotBlank() == true) {
                        InfoRow(icon = Icons.Default.LocationOn, label = "Province", value = user.province)
                    }
                    if (user.district?.isNotBlank() == true) {
                        InfoRow(icon = Icons.Default.LocationOn, label = "District", value = user.district)
                    }
                    if (user.whatsapp?.isNotBlank() == true) {
                        InfoRow(icon = Icons.Default.Phone, label = "WhatsApp", value = user.whatsapp)
                    }
                } else {
                    Text(
                        text = "No user data available",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Language & Region Section
            ExpandableSection(
                title = "Language & Region",
                icon = Icons.Default.Settings,
                expanded = expandedSection == ProfileSection.LANGUAGE,
                onToggle = {
                    expandedSection = if (expandedSection == ProfileSection.LANGUAGE) null else ProfileSection.LANGUAGE
                }
            ) {
                val language = currentUser?.language
                if (language?.isNotBlank() == true) {
                    InfoRow(icon = Icons.Default.Star, label = "Language", value = language)
                } else {
                    Text(
                        text = "Language not set",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button
            AppButton(
                text = "Logout",
                textColor = MaterialTheme.colorScheme.error,
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                    disabledContentColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                ),
                onClick = {
                    authViewModel.logout()
                    navController.navigate(Graph.AUTHENTICATION) {
                        popUpTo(Graph.BASE) { inclusive = true }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // App Version
            Text(
                text = "eMaliEstates v1.0.0",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProfileHeaderCard(
    user: User?,
    onEditProfile: () -> Unit
) {
    val backgroundColor = MaterialTheme.colorScheme.primaryContainer

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            ) {
                if (user?.profileImg?.isNotBlank() == true) {
                    // TODO: Load image with Coil
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile picture",
                        modifier = Modifier.fillMaxSize(),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile picture",
                        modifier = Modifier
                            .size(80.dp)
                            .padding(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Name
            Text(
                text = user?.name ?: "Guest User",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            // User Role
            if (user?.role?.isNotBlank() == true) {
                Text(
                    text = user.role,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // User Email
            if (user?.email?.isNotBlank() == true) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = user.email,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Edit Profile Button
            OutlinedButton(
                onClick = onEditProfile,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Edit Profile")
            }
        }
    }
}

@Composable
fun ExpandableSection(
    title: String,
    icon: ImageVector,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 52.dp, top = 8.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    Divider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        thickness = 1.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    content()
                }
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(24.dp)
                    .padding(8.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun InfoRow(
    icon: ImageVector? = null,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

enum class ProfileSection {
    ACCOUNT,
    PERSONAL,
    LANGUAGE
}

@Preview
@Composable
fun ProfileScreenPreview() {
    EmaliEstatesTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ProfileScreen(navController = androidx.navigation.compose.rememberNavController())
        }
    }
}

package com.shortspark.emaliestates.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.shortspark.emaliestates.domain.auth.User
import com.shortspark.emaliestates.home.viewModel.ProfileViewModel
import org.koin.compose.viewmodel.koinViewModel
import com.shortspark.emaliestates.util.components.common.CommonOutlinedTextField

// ─── Theme Colors (using MaterialTheme) ───────────────────────────────────────

@Composable
private fun Orange(): Color = MaterialTheme.colorScheme.secondary

@Composable
private fun Background(): Color = MaterialTheme.colorScheme.background

@Composable
private fun Surface(): Color = MaterialTheme.colorScheme.surface

@Composable
private fun TextPrimary(): Color = MaterialTheme.colorScheme.onBackground

@Composable
private fun TextSecondary(): Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

// ─── Edit Profile Screen ─────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.profileState.collectAsState()
    val currentUser = viewModel.currentUser

    // Initialize form state from current user
    var fullName by remember { mutableStateOf(currentUser?.name ?: "") }
    var phone by remember { mutableStateOf(currentUser?.phone ?: "") }

    var saveClicked by remember { mutableStateOf(false) }

    // Show error snackbar/dialog if needed
    if (state.errorMessage != null) {
        LaunchedEffect(state.errorMessage) {
            // TODO: Show snackbar or error dialog
            viewModel.clearError()
        }
    }

    // Handle successful update - navigate back
    LaunchedEffect(state.isLoading) {
        if (saveClicked && !state.isLoading && state.errorMessage == null) {
            saveClicked = false
            navController.popBackStack()
        }
    }

    Scaffold(
        containerColor = Background()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Top bar ───────────────────────────────────────────────────────
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
                        tint = TextPrimary()
                    )
                }
                Text(
                    text = "Edit Profile",
                    color = TextPrimary(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = {
                        saveClicked = true
                        viewModel.updateProfile(
                            fullName = fullName.ifBlank { null },
                            phone = phone.ifBlank { null }
                        )
                    },
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Orange()
                        )
                    } else {
                        Text(
                            text = "Save",
                            color = Orange(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Avatar section ────────────────────────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .border(3.dp, Orange(), CircleShape)
                        .clickable { /* TODO: Pick image */ }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = TextSecondary(),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Change Photo",
                    color = Orange(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { /* TODO: Pick image */ }
                )
            }

            Spacer(Modifier.height(32.dp))

            // ── Form fields ───────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "Personal Information",
                    color = TextPrimary(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(16.dp))

                // Full Name Field
                var fullNameFocusState by remember { mutableStateOf(false) }
                CommonOutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    isFocused = fullNameFocusState,
                    onFocusChange = { fullNameFocusState = it.isFocused },
                    label = "Full Name",
                    placeholder = "",
                    leadingIcon = null,
                    trailingIcon = null,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // Email Field (read-only)
                var emailFocusState by remember { mutableStateOf(false) }
                CommonOutlinedTextField(
                    value = currentUser?.email ?: "",
                    onValueChange = {},
                    isFocused = emailFocusState,
                    onFocusChange = { emailFocusState = it.isFocused },
                    label = "Email",
                    placeholder = "",
                    leadingIcon = null,
                    trailingIcon = null,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // Phone Field
                var phoneFocusState by remember { mutableStateOf(false) }
                CommonOutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    isFocused = phoneFocusState,
                    onFocusChange = { phoneFocusState = it.isFocused },
                    label = "Phone",
                    placeholder = "Add phone number",
                    leadingIcon = null,
                    trailingIcon = null,
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

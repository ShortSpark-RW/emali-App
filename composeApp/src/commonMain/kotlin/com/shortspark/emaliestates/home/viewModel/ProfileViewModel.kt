package com.shortspark.emaliestates.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shortspark.emaliestates.data.AuthSDK
import com.shortspark.emaliestates.data.PropertySDK
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.domain.RequestState
import com.shortspark.emaliestates.domain.auth.AuthData
import com.shortspark.emaliestates.domain.auth.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI state for Profile screen
 */
data class ProfileState(
    val user: RequestState<User> = RequestState.Idle,
    val userProperties: RequestState<List<Property>> = RequestState.Idle,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ProfileViewModel(
    private val authSDK: AuthSDK,
    private val propertySDK: PropertySDK
) : ViewModel() {

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    val currentUser: User? get() = (_profileState.value.user as? RequestState.Success)?.data
    val properties: List<Property> get() = (_profileState.value.userProperties as? RequestState.Success)?.data ?: emptyList()

    // Stats computed from properties
    val totalProperties: Int get() = properties.size
    val soldProperties: Int get() = properties.count { it.isSold || it.isRented }
    val favoriteCount: Int get() = 0 // TODO: implement favorites

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState(isLoading = true)

            // For now, we'll use sample/mock user since getCurrentUser() is not implemented
            // TODO: When backend has /auth/me endpoint, use:
            // val userResult = authSDK.getCurrentUser()
            // and filter properties by current user's ID

            // Load all properties and show as user's properties for demo
            // In production, filter by properties where ownerId == currentUser.id
            val propertiesResult = propertySDK.getAllProperties()

            _profileState.value = ProfileState(
                user = RequestState.Success(
                    User(
                        id = "current-user",
                        email = "user@example.com",
                        name = "Kimenyi Yvan",
                        phone = "+250 123 456 789",
                        username = "kimenyiyvan",
                        role = "user",
                        isActive = true,
                        isVerified = true
                    )
                ),
                userProperties = propertiesResult,
                isLoading = false
            )
        }
    }

    fun refreshProfile() {
        loadProfile()
    }

    fun updateProfile(
        fullName: String? = null,
        phone: String? = null,
        gender: String? = null,
        dateOfBirth: String? = null,
        locationId: String? = null
    ) {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true, errorMessage = null)

            val currentUserData = (_profileState.value.user as? RequestState.Success)?.data
            val nameToUpdate = fullName ?: currentUserData?.name
            val phoneToUpdate = phone ?: currentUserData?.phone

            val result = authSDK.updateProfile(
                fullName = nameToUpdate,
                phone = phoneToUpdate,
                gender = gender,
                dateOfBirth = dateOfBirth,
                locationId = locationId
            )

            when (result) {
                is RequestState.Success -> {
                    val updatedUser = (result.data as? com.shortspark.emaliestates.domain.auth.AuthData)?.user
                    if (updatedUser != null) {
                        _profileState.value = _profileState.value.copy(
                            user = RequestState.Success(updatedUser),
                            isLoading = false
                        )
                    } else {
                        loadProfile()
                    }
                }
                is RequestState.Error -> {
                    _profileState.value = _profileState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                else -> {
                    _profileState.value = _profileState.value.copy(isLoading = false)
                }
            }
        }
    }

    fun clearError() {
        _profileState.value = _profileState.value.copy(errorMessage = null)
    }
}

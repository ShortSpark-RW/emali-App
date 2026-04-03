package com.shortspark.emaliestates.util.helpers

/**
 * Centralized UI strings.
 * For full localization, migrate to Compose Multiplatform Resources.
 */
object AppStrings {
    // Auth
    const val WELCOME_BACK = "Welcome Back"
    const val SIGN_IN_TO_ACCOUNT = "Sign in to your account"
    const val SIGN_IN = "Sign In"
    const val SIGN_UP = "Sign up"
    const val FORGOT_PASSWORD = "Forgot Password?"
    const val REMEMBER_ME = "Remember me"
    const val DONT_HAVE_ACCOUNT = "Don't have an account?"
    const val EMAIL_REQUIRED = "Email is required"
    const val VALID_EMAIL_REQUIRED = "Enter a valid email address"
    const val PASSWORD_REQUIRED = "Password is required"
    const val PASSWORD_TOO_SHORT = "Password must be at least 8 characters long"
    const val PASSWORD_COMPLEXITY = "Password must contain an uppercase letter, a number, and a special character"
    const val GOOGLE_SIGN_IN = "Continue with Google"
    const val OR = "Or"

    // Errors
    const val UNKNOWN_ERROR = "An unexpected error occurred. Please try again."
    const val NETWORK_ERROR = "No internet connection. Please check your network settings."
    const val SERVER_ERROR = "Server is currently unavailable. Please try again in a moment."
    const val INVALID_CREDENTIALS = "Invalid email or password. Please check your credentials."
    const val SESSION_EXPIRED = "Your session has expired. Please log in again."
    const val GOOGLE_SIGN_IN_CANCELLED = "Google sign-in was cancelled."

    // Home
    const val HOME = "Home"
    const val MAP = "Map"
    const val TOURS = "Tours"
    const val SEARCH = "Search"
    const val PROFILE = "Profile"

    // Property
    const val PROPERTY_DETAILS = "Property Details"
    const val SIMILAR_PROPERTIES = "Similar Properties"
    const val SCHEDULE_TOUR = "Schedule Tour"

    // Generic
    const val LOADING = "Loading..."
    const val RETRY = "Retry"
    const val CANCEL = "Cancel"
    const val OK = "OK"
}

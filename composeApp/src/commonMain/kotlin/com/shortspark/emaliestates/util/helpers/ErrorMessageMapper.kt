package com.shortspark.emaliestates.util.helpers

/**
 * Maps technical error messages to user-friendly ones.
 * Centralizes error handling for consistent UX.
 */
object ErrorMessageMapper {

    /**
     * Returns a user-friendly error message for display.
     * null = generic error message.
     */
    fun getFriendlyMessage(error: String?): String {
        if (error.isNullOrBlank()) return "An unexpected error occurred. Please try again."

        val lowerError = error.lowercase()

        return when {
            // Network errors
            lowerError.contains("timeout") ||
            lowerError.contains("unable to resolve host") ||
            lowerError.contains("network") ||
            lowerError.contains("internet") -> {
                "No internet connection. Please check your network settings."
            }
            lowerError.contains("connection refused") ||
            lowerError.contains("failed to connect") -> {
                "Cannot connect to server. Please try again later."
            }

            // Authentication errors
            lowerError.contains("unauthorized") ||
            lowerError.contains("invalid credentials") ||
            lowerError.contains("wrong password") ||
            lowerError.contains("user not found") -> {
                "Invalid email or password. Please check your credentials."
            }
            lowerError.contains("token") ||
            lowerError.contains("expired") -> {
                "Your session has expired. Please log in again."
            }

            // Server errors
            lowerError.contains("internal server error") ||
            lowerError.contains("500") -> {
                "Server is currently unavailable. Please try again in a moment."
            }
            lowerError.contains("service unavailable") ||
            lowerError.contains("503") -> {
                "Service is temporarily down for maintenance. Please try again later."
            }

            // Validation errors
            lowerError.contains("required") ||
            lowerError.contains("must not be blank") -> {
                "Please fill in all required fields."
            }
            lowerError.contains("invalid email") ||
            lowerError.contains("email is invalid") -> {
                "Please enter a valid email address."
            }
            lowerError.contains("password") && lowerError.contains("length") -> {
                "Password must be at least 8 characters long."
            }

            // Google auth errors
            lowerError.contains("google") &&
            (lowerError.contains("cancel") || lowerError.contains("abort")) -> {
                "Google sign-in was cancelled."
            }
            lowerError.contains("google id token") -> {
                "Google authentication failed. Please try again."
            }

            // Default: return sanitized original message (remove potential PII)
            else -> {
                // Sanitize: don't echo back raw server errors that might contain sensitive data
                "An error occurred. Please try again or contact support."
            }
        }
    }
}

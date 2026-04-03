package com.shortspark.emaliestates.domain.auth.validation

/**
 * Result of a validation operation.
 * Valid: null error message
 * Invalid: error message explaining what's wrong
 */
typealias ValidationResult = String?

/**
 * Contains all validation rules for authentication forms.
 * Centralized for reusability and testability.
 */
object ValidationRules {

    private val EMAIL_REGEX = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
    private val PASSWORD_REGEX = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}\$")

    /**
     * Validates an email address.
     * @return null if valid, error message if invalid
     */
    fun validateEmail(email: String): ValidationResult = when {
        email.isBlank() -> "Email is required"
        !EMAIL_REGEX.matches(email) -> "Enter a valid email address"
        else -> null
    }

    /**
     * Validates a password.
     * Requirements:
     * - At least 8 characters long
     * - Contains at least one lowercase letter
     * - Contains at least one uppercase letter
     * - Contains at least one digit
     * - Contains at least one special character (@$!%*?&)
     */
    fun validatePassword(password: String): ValidationResult = when {
        password.isBlank() -> "Password is required"
        password.length < 8 -> "Password must be at least 8 characters long"
        !PASSWORD_REGEX.matches(password) -> "Password must contain uppercase, number, and special character (@\$!%*?&)"
        else -> null
    }

    /**
     * Validates that two passwords match.
     */
    fun validatePasswordMatch(password: String, confirmPassword: String): ValidationResult {
        return if (password != confirmPassword) {
            "Passwords do not match"
        } else {
            null
        }
    }

    /**
     * Validates all fields for sign-in (email + password).
     */
    fun validateSignIn(email: String, password: String): Map<String, ValidationResult> {
        return mapOf(
            "email" to validateEmail(email),
            "password" to validatePassword(password)
        )
    }

    /**
     * Validates all fields for sign-up (email + password + confirmPassword).
     */
    fun validateSignUp(
        email: String,
        password: String,
        confirmPassword: String
    ): Map<String, ValidationResult> {
        return mapOf(
            "email" to validateEmail(email),
            "password" to validatePassword(password),
            "confirmPassword" to validatePasswordMatch(password, confirmPassword)
        )
    }
}

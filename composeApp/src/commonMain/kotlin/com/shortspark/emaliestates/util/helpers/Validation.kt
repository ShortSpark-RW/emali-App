package com.shortspark.emaliestates.util.helpers

import kotlin.text.Regex

/**
 * Validation utility for common form validations
 */
object Validation {

    /**
     * Validates email format
     * @param email Email to validate
     * @return Error message if invalid, null if valid
     */
    fun validateEmail(email: String): String? {
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return when {
            email.isBlank() -> "Email is required"
            !emailRegex.matches(email) -> "Enter a valid email address"
            else -> null
        }
    }

    /**
     * Validates password strength
     * @param password Password to validate
     * @return Error message if invalid, null if valid
     */
    fun validatePassword(password: String): String? {
        val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
        return when {
            password.isBlank() -> "Password is required"
            password.length < 8 -> "Password must be at least 8 characters long"
            !passwordRegex.matches(password) -> "Password must contain an uppercase letter, a number, and a special character"
            else -> null
        }
    }

    /**
     * Validates phone number format
     * @param phone Phone number to validate
     * @return Error message if invalid, null if valid
     */
    fun validatePhone(phone: String): String? {
        if (phone.isBlank()) return "Phone number is required"
        // Simple validation - adjust based on your region requirements
        val digitsOnly = phone.replace(Regex("[^0-9]"), "")
        return when {
            digitsOnly.length < 10 -> "Phone number must be at least 10 digits"
            else -> null
        }
    }

    /**
     * Validates that a field is not empty
     * @param value Value to validate
     * @param fieldName Name of the field for error message
     * @return Error message if empty, null if valid
     */
    fun validateRequired(value: String, fieldName: String): String? {
        return if (value.isBlank()) "$fieldName is required" else null
    }

    /**
     * Validates that a value is within a specified range
     * @param value Value to validate
     * @param min Minimum allowed value (inclusive)
     * @param max Maximum allowed value (inclusive)
     * @param fieldName Name of the field for error message
     * @return Error message if invalid, null if valid
     */
    fun validateRange(value: Int, min: Int, max: Int, fieldName: String): String? {
        return when {
            value < min -> "$fieldName must be at least $min"
            value > max -> "$fieldName must be at most $max"
            else -> null
        }
    }
}
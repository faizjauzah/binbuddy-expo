package com.example.expopab.utils

import android.util.Patterns

// ValidationUtils.kt
// Utility class for input validation
object ValidationUtils {
    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        // Minimum 8 characters, at least one letter and one number
        val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}\$".toRegex()
        return passwordPattern.matches(password)
    }
}
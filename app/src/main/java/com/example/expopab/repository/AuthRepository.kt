package com.example.expopab.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

// AuthRepository.kt
// This class handles all Firebase authentication operations
class AuthRepository {
    private val auth: FirebaseAuth = Firebase.auth

    // Wrapping Firebase result in a sealed class for better error handling
    sealed class AuthResult {
        object Success : AuthResult()
        data class Error(val message: String) : AuthResult()
    }

    suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Authentication failed")
        }
    }

    suspend fun signUp(email: String, password: String): AuthResult {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Registration failed")
        }
    }
}

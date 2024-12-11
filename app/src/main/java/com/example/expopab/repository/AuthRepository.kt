package com.example.expopab.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthRepository {
    sealed class AuthResult {
        object Success : AuthResult()
        object Loading : AuthResult()
        data class Error(val message: String) : AuthResult()
    }

    suspend fun signUp(name: String, email: String, password: String): AuthResult {
        return try {
            // First create the authentication account
            val authResult = Firebase.auth.createUserWithEmailAndPassword(email, password).await()

            // If authentication successful, save user data
            authResult.user?.let { user ->
                try {
                    val userData = hashMapOf(
                        "fullName" to name,
                        "email" to email
                    )

                    // Save to Firestore
                    Firebase.firestore.collection("users")
                        .document(user.uid)
                        .set(userData)
                        .await()

                    AuthResult.Success
                } catch (e: Exception) {
                    // If Firestore save fails, delete the authentication account
                    user.delete().await()
                    AuthResult.Error("Failed to save user data: ${e.message}")
                }
            } ?: AuthResult.Error("Failed to create user")

        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("PERMISSION_DENIED") == true ->
                    "Permission denied. Please try again or contact support."
                else -> e.message ?: "Sign up failed"
            }
            AuthResult.Error(errorMessage)
        }
    }

    suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            Firebase.auth.signInWithEmailAndPassword(email, password).await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Sign in failed")
        }
    }
}
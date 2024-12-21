package com.example.expopab.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.expopab.databinding.ActivitySettingsBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val db = Firebase.firestore
    private val currentUser = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        setupToolbar()

        // Load current user's name
        loadCurrentUserName()

        binding.buttonSaveChanges.setOnClickListener {
            lifecycleScope.launch {
                saveChanges()
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Handle back button click
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun loadCurrentUserName() {
        currentUser?.let { user ->
            db.collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        binding.editTextFullName.setText(document.getString("fullName"))
                    }
                }
        }
    }

    private suspend fun saveChanges() {
        try {
            val newFullName = binding.editTextFullName.text.toString().trim()
            val currentPassword = binding.editTextCurrentPassword.text.toString()
            val newPassword = binding.editTextNewPassword.text.toString()

            if (newFullName.isEmpty()) {
                binding.editTextFullName.error = "Full name cannot be empty"
                return
            }

            currentUser?.let { user ->
                // If user wants to change password
                if (newPassword.isNotEmpty()) {
                    if (currentPassword.isEmpty()) {
                        binding.editTextCurrentPassword.error = "Please enter current password"
                        return
                    }

                    if (newPassword.length < 6) {
                        binding.editTextNewPassword.error = "Password must be at least 6 characters"
                        return
                    }

                    // Verify current password
                    try {
                        val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
                        user.reauthenticate(credential).await()
                        // Update password
                        user.updatePassword(newPassword).await()
                    } catch (e: Exception) {
                        binding.editTextCurrentPassword.error = "Incorrect current password"
                        return
                    }
                }

                // Update name in Firestore
                db.collection("users")
                    .document(user.uid)
                    .update("fullName", newFullName)
                    .await()

                Toast.makeText(this, "Changes saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
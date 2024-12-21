package com.example.expopab.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.expopab.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class SignOutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_out)

        // Find the buttons
        val signOutButton = findViewById<Button>(R.id.confirmSignOutButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)

        // Handle sign out
        signOutButton.setOnClickListener {
            try {
                // Sign out from Firebase
                Firebase.auth.signOut()

                // Navigate to Welcome activity
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                // Handle any errors
                Toast.makeText(this, "Sign out failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle cancel
        cancelButton.setOnClickListener {
            // Simply finish the activity to go back
            finish()
        }
    }
}
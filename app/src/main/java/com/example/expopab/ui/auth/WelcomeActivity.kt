package com.example.expopab.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.expopab.R

// WelcomeActivity.kt
class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Initialize buttons
        val signInButton = findViewById<Button>(R.id.signInButton)
        val signUpButton = findViewById<Button>(R.id.signUpButton)

        // Set click listeners
        signInButton.setOnClickListener {
            // Navigate to Sign In activity
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        signUpButton.setOnClickListener {
            // Navigate to Sign Up activity
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}
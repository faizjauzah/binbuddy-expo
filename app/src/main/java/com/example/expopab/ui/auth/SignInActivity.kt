package com.example.expopab.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.expopab.MainActivity
import com.example.expopab.databinding.ActivitySignInBinding  // Import the generated binding class
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SignInActivity : AppCompatActivity() {
    // Declare our binding variable at the class level so it's accessible throughout the activity
    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the binding by inflating the layout
        binding = ActivitySignInBinding.inflate(layoutInflater)
        // Set the root view of the binding as the content view
        setContentView(binding.root)

        auth = Firebase.auth

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Access views through the binding object
        binding.signInButton.setOnClickListener {
            // Get text from EditText views through binding
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (validateInput(email, password)) {
                signIn(email, password)
            }
        }

        binding.signUpButton.setOnClickListener {
            // Navigate to SignUp Activity
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Navigate to Main Activity
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}
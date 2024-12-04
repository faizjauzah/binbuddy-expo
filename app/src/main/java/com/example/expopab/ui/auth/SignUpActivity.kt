package com.example.expopab.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.expopab.MainActivity
import com.example.expopab.R
import com.example.expopab.repository.AuthRepository.AuthResult
import com.example.expopab.utils.ValidationUtils
import com.example.expopab.viewmodel.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import com.example.expopab.databinding.ActivitySignUpBinding  // ViewBinding import

class SignUpActivity : AppCompatActivity() {
    // Initialize ViewBinding - this replaces the synthetic properties
    private lateinit var binding: ActivitySignUpBinding

    // Initialize ViewModel using the by viewModels() delegate
    // This fixes the "Unresolved reference: viewModels" error
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set up ViewBinding - this replaces setContentView(R.layout.activity_sign_up)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.authState.observe(this) { result ->
            when (result) {
                is AuthResult.Success -> {
                    // Navigate to MainActivity
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity() // Clear activity stack
                }
                is AuthResult.Error -> {
                    showError(result.message)
                }
            }
        }
    }

    private fun setupClickListeners() {
        // Use binding to access views - this fixes the unresolved reference errors
        binding.signUpButton.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (validateInput(name, email, password)) {
                viewModel.signUp(email, password)
            }
        }

        binding.backToSignInButton.setOnClickListener {
            finish() // Return to SignIn screen
        }
    }

    private fun validateInput(name: String, email: String, password: String): Boolean {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("Please fill all fields")
            return false
        }
        if (!ValidationUtils.isValidEmail(email)) {
            showError("Please enter a valid email")
            return false
        }
        if (!ValidationUtils.isValidPassword(password)) {
            showError("Password must be at least 8 characters long and contain both letters and numbers")
            return false
        }
        return true
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}
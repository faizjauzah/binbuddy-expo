package com.example.expopab.viewmodel

// Essential imports for ViewModel functionality
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch

// Import our custom Repository and AuthResult
import com.example.expopab.repository.AuthRepository
import com.example.expopab.repository.AuthRepository.AuthResult

// AuthViewModel extends ViewModel() to get lifecycle awareness
class AuthViewModel : ViewModel() {
    // Create an instance of our repository to handle authentication operations
    private val repository = AuthRepository()

    // Private MutableLiveData that can be modified within the ViewModel
    private val _authState = MutableLiveData<AuthResult>()

    // Public LiveData that can only be observed, not modified
    // This ensures data integrity by preventing external modifications
    val authState: LiveData<AuthResult> = _authState

    // Function to handle sign-in process
    fun signIn(email: String, password: String) {
        // Launch a coroutine in the ViewModel's scope
        viewModelScope.launch {
            // Call repository's signIn function and store the result
            val result = repository.signIn(email, password)
            // Update the LiveData with the result
            _authState.value = result
        }
    }

    // Function to handle sign-up process
    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.signUp(email, password)
            _authState.value = result
        }
    }
}
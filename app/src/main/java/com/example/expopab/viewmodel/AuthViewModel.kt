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
    private val repository = AuthRepository()
    private val _authState = MutableLiveData<AuthResult>()
    val authState: LiveData<AuthResult> = _authState

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.signIn(email, password)
            _authState.value = result
        }
    }

    // Modified to include name parameter
    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            val result = repository.signUp(name, email, password)
            _authState.value = result
        }
    }
}
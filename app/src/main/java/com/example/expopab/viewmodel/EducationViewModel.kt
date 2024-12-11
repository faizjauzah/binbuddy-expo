package com.example.expopab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expopab.repository.EducationalContentRepository
import com.example.expopab.ui.home.EducationUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EducationViewModel : ViewModel() {
    private val repository = EducationalContentRepository()

    private val _uiState = MutableStateFlow<EducationUIState>(EducationUIState.Loading)
    val uiState: StateFlow<EducationUIState> = _uiState.asStateFlow()

    // Function for HomeFragment (3 items)
    fun loadPreviewContent() {
        viewModelScope.launch {
            try {
                repository.getEducationalContent().collect { content ->
                    _uiState.value = if (content.isEmpty()) {
                        EducationUIState.Empty
                    } else {
                        EducationUIState.Success(content.take(3))
                    }
                }
            } catch (e: Exception) {
                _uiState.value = EducationUIState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    // Function for EducationFragment (all items)
    fun loadAllContent() {
        viewModelScope.launch {
            try {
                repository.getEducationalContent().collect { content ->
                    _uiState.value = if (content.isEmpty()) {
                        EducationUIState.Empty
                    } else {
                        EducationUIState.Success(content)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = EducationUIState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}
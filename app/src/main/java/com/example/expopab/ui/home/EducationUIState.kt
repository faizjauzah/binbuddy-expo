package com.example.expopab.ui.home

import com.example.expopab.model.EducationalContent


sealed class EducationUIState {
    object Loading : EducationUIState()
    data class Success(val content: List<EducationalContent>) : EducationUIState()
    data class Error(val message: String) : EducationUIState()
    object Empty : EducationUIState()
}

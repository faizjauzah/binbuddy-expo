package com.example.expopab.model

data class EducationalContent(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val category: String = "",
    val articleContent: String = ""  // Add this new field for the article
) {
    // Empty constructor for Firestore
    constructor() : this("", "", "", "", "", "")
}
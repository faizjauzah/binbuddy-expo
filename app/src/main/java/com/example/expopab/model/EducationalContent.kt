package com.example.expopab.model

data class EducationalContent(
    val id: String = "",  // Making properties optional with default values
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val category: String = ""
) {
    // Empty constructor for Firestore
    constructor() : this("", "", "", "", "")
}

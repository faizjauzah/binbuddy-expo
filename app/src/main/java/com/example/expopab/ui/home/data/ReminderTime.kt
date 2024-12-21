package com.example.expopab.ui.home.data

import java.util.UUID

data class ReminderTime(
    val id: String = UUID.randomUUID().toString(),
    val hour: Int = 0,
    val minute: Int = 0,
    val userId: String? = null,
    val selectedDays: List<Int> = listOf() // 0 = Sunday, 1 = Monday, etc.
)
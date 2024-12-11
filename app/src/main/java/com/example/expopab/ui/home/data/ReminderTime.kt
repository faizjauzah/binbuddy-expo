package com.example.expopab.ui.home.data

import java.util.UUID

data class ReminderTime(
    val id: String = UUID.randomUUID().toString(),
    val hour: Int = 0,
    val minute: Int = 0,
    val userId: String? = null
)
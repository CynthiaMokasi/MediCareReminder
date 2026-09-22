package com.example.medicareReminder.api

data class AuthResponse(
    val message: String,
    val userId: Int,
    val fullName: String,
    val email: String
)
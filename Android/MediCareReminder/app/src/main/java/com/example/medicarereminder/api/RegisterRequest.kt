package com.example.medicareReminder.api

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String
)
package com.example.medicareReminder.api

data class RegisterResponse(
    val message: String,
    val userId: Int,
    val fullName: String,
    val email: String
)
data class Medication(
    val medicationId: Int = 0,
    val userId: Int,
    val name: String,
    val dosage: String,
    val frequency: String,
    val reminderTime: String
)

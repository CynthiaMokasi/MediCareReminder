package com.example.medicareReminder.api

data class RegisterResponse(
    val message: String? = null,
    val userId: Int? = null,
    val fullName: String? = null,
    val email: String? = null
)
data class Medication(
    val medicationId: Int = 0,
    val userId: Int,
    val name: String,
    val dosage: String,
    val frequency: String,
    val reminderTime: String
)

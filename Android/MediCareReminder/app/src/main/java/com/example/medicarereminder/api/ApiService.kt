package com.example.medicareReminder.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    // =========================
    // AUTHENTICATION
    // =========================

    @POST("api/Auth/register")
    fun register(
        @Body request: RegisterRequest
    ): Call<RegisterResponse>

    @POST("api/Auth/login")
    fun login(
        @Body request: LoginRequest
    ): Call<AuthResponse>

    @POST("api/Auth/google")
    fun googleLogin(
        @Body request: GoogleLoginRequest
    ): Call<AuthResponse>


    // =========================
    // MEDICATIONS
    // =========================

    @GET("api/Medications/{userId}")
    fun getMedications(
        @Path("userId") userId: Int
    ): Call<List<Medication>>

    @POST("api/Medications")
    fun addMedication(
        @Body medication: Medication
    ): Call<Medication>

    @PUT("api/Medications/{id}")
    fun updateMedication(
        @Path("id") medicationId: Int,
        @Body medication: Medication
    ): Call<Medication>

    @DELETE("api/Medications/{id}")
    fun deleteMedication(
        @Path("id") medicationId: Int
    ): Call<Void>


    // =========================
    // USER SETTINGS - COSMOS DB
    // =========================

    @GET("api/Settings/{userId}")
    fun getSettings(
        @Path("userId") userId: String
    ): Call<UserSettings>

    @PUT("api/Settings/{userId}")
    fun saveSettings(
        @Path("userId") userId: String,
        @Body settings: UserSettings
    ): Call<UserSettings>
}


package com.example.medicareReminder.api

import com.google.gson.annotations.SerializedName

data class UserSettings(
    @SerializedName("id")
    val id: String = "",

    @SerializedName("userId")
    val userId: String = "",

    @SerializedName("notificationsEnabled")
    val notificationsEnabled: Boolean = true,

    @SerializedName("darkMode")
    val darkMode: Boolean = false,

    @SerializedName("language")
    val language: String = "en",

    @SerializedName("offlineSyncEnabled")
    val offlineSyncEnabled: Boolean = true
)

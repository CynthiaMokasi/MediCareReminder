
package com.example.medicareReminder

import android.content.Context

class SettingsManager(context: Context) {

    private val preferences = context.getSharedPreferences(
        "MediCareReminderSettings",
        Context.MODE_PRIVATE
    )

    var notificationsEnabled: Boolean
        get() = preferences.getBoolean("notificationsEnabled", true)
        set(value) {
            preferences.edit()
                .putBoolean("notificationsEnabled", value)
                .apply()
        }

    var darkModeEnabled: Boolean
        get() = preferences.getBoolean("darkModeEnabled", false)
        set(value) {
            preferences.edit()
                .putBoolean("darkModeEnabled", value)
                .apply()
        }

    var language: String
        get() = preferences.getString("language", "en") ?: "en"
        set(value) {
            val supportedLanguages = setOf("en", "zu", "xi")
            if (value in supportedLanguages) {
                preferences.edit()
                    .putString("language", value)
                    .apply()
            }
        }

    var offlineSyncEnabled: Boolean
        get() = preferences.getBoolean("offlineSyncEnabled", true)
        set(value) {
            preferences.edit()
                .putBoolean("offlineSyncEnabled", value)
                .apply()
        }
}

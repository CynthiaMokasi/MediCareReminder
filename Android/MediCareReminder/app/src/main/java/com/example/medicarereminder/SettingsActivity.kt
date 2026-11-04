
package com.example.medicareReminder

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.medicareReminder.api.ApiClient
import com.example.medicareReminder.api.UserSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsActivity : AppCompatActivity() {

    private lateinit var notifications: Switch
    private lateinit var reminderSound: Switch
    private lateinit var spinnerLanguage: Spinner
    private lateinit var preferences:
            android.content.SharedPreferences

    private var currentSettings = UserSettings()
    private var currentUserId: String? = null
    private var isLoadingSettings = true
    private var isUpdatingLanguageSpinner = false

    private val languageCodes = listOf("en", "ts", "zu")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        preferences = getSharedPreferences(
            "MediCarePrefs",
            MODE_PRIVATE
        )

        val back = findViewById<TextView>(R.id.txtSettingsBack)
        notifications = findViewById(R.id.switchNotifications)
        reminderSound = findViewById(R.id.switchReminderSound)
        spinnerLanguage = findViewById(R.id.spinnerLanguage)
        val logout = findViewById<Button>(R.id.btnLogout)
        val email = findViewById<TextView>(R.id.txtAccountEmail)

        back.setOnClickListener {
            finish()
        }

        email.text = preferences.getString(
            "email",
            "user@example.com"
        )

        // Restore the locally saved language.
        val savedLanguage = preferences.getString(
            "language",
            "en"
        ) ?: "en"

        setupLanguageSpinner(savedLanguage)

        // Restore the notification preferences.
        notifications.isChecked = preferences.getBoolean(
            "notifications",
            true
        )

        reminderSound.isChecked = preferences.getBoolean(
            "reminderSound",
            true
        )

        // Save reminder sound locally.
        reminderSound.setOnCheckedChangeListener { _, checked ->
            preferences.edit()
                .putBoolean("reminderSound", checked)
                .apply()
        }

        // Get the actual logged-in user's ID.
        val savedUserId = preferences.getInt("userId", -1)

        if (savedUserId == -1) {
            isLoadingSettings = false

            Toast.makeText(
                this,
                "Please log in to load your settings.",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        currentUserId = savedUserId.toString()

        // Load settings from the API and Cosmos DB.
        loadSettings()

        // Save notification changes locally and in Cosmos DB.
        notifications.setOnCheckedChangeListener { _, checked ->
            preferences.edit()
                .putBoolean("notifications", checked)
                .apply()

            if (!isLoadingSettings) {
                saveNotificationSetting(checked)
            }
        }

        // Logout.
        logout.setOnClickListener {
            preferences.edit()
                .clear()
                .apply()

            val intent = Intent(
                this,
                MainActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }
    }

    private fun setupLanguageSpinner(savedLanguage: String) {

        val languageNames = listOf(
            getString(R.string.english),
            getString(R.string.xitsonga),
            getString(R.string.isizulu)
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            languageNames
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerLanguage.adapter = adapter

        val selectedPosition =
            languageCodes.indexOf(savedLanguage).takeIf {
                it >= 0
            } ?: 0

        isUpdatingLanguageSpinner = true
        spinnerLanguage.setSelection(selectedPosition)
        isUpdatingLanguageSpinner = false

        // Apply a previously saved non-default language.
        if (savedLanguage != "en") {
            applyLanguage(savedLanguage)
        }

        spinnerLanguage.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {
                    if (isUpdatingLanguageSpinner) return

                    val selectedLanguage = languageCodes[position]

                    val previousLanguage = preferences.getString(
                        "language",
                        "en"
                    )

                    if (selectedLanguage == previousLanguage) {
                        return
                    }

                    // Save the selected language locally.
                    preferences.edit()
                        .putString("language", selectedLanguage)
                        .apply()

                    // Apply the selected app language.
                    applyLanguage(selectedLanguage)

                    // Save the language in cloud settings.
                    saveLanguageSetting(selectedLanguage)

                    Toast.makeText(
                        this@SettingsActivity,
                        "Language changed successfully.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onNothingSelected(
                    parent: android.widget.AdapterView<*>?
                ) {
                    // No action needed.
                }
            }
    }

    private fun applyLanguage(languageCode: String) {

        val currentLanguage =
            AppCompatDelegate.getApplicationLocales()
                .toLanguageTags()

        if (currentLanguage != languageCode) {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(languageCode)
            )
        }
    }

    private fun loadSettings() {
        val userId = currentUserId ?: return

        ApiClient.apiService.getSettings(userId)
            .enqueue(object : Callback<UserSettings> {

                override fun onResponse(
                    call: Call<UserSettings>,
                    response: Response<UserSettings>
                ) {
                    if (response.isSuccessful) {
                        val settings = response.body()

                        if (settings != null) {
                            currentSettings = settings

                            isLoadingSettings = true

                            notifications.isChecked =
                                settings.notificationsEnabled

                            preferences.edit()
                                .putBoolean(
                                    "notifications",
                                    settings.notificationsEnabled
                                )
                                .apply()

                            // Restore the cloud-saved language.
                            val cloudLanguage =
                                settings.language.ifBlank {
                                    preferences.getString(
                                        "language",
                                        "en"
                                    ) ?: "en"
                                }

                            preferences.edit()
                                .putString("language", cloudLanguage)
                                .apply()

                            val languagePosition =
                                languageCodes.indexOf(cloudLanguage)
                                    .takeIf { it >= 0 } ?: 0

                            isUpdatingLanguageSpinner = true
                            spinnerLanguage.setSelection(languagePosition)
                            isUpdatingLanguageSpinner = false

                            if (cloudLanguage in languageCodes) {
                                applyLanguage(cloudLanguage)
                            }

                            isLoadingSettings = false

                        } else {
                            isLoadingSettings = false
                        }

                    } else if (response.code() == 404) {

                        // Create cloud settings using local preferences.
                        currentSettings = UserSettings(
                            id = userId,
                            userId = userId,
                            notificationsEnabled =
                                preferences.getBoolean(
                                    "notifications",
                                    true
                                ),
                            darkMode = false,
                            language = preferences.getString(
                                "language",
                                "en"
                            ) ?: "en",
                            offlineSyncEnabled = true
                        )

                        saveInitialSettings()

                    } else {
                        isLoadingSettings = false

                        Toast.makeText(
                            this@SettingsActivity,
                            "Could not load settings. HTTP ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<UserSettings>,
                    t: Throwable
                ) {
                    isLoadingSettings = false

                    Toast.makeText(
                        this@SettingsActivity,
                        "Unable to load cloud settings. " +
                                (t.message ?: "Network error"),
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun saveInitialSettings() {
        val userId = currentUserId ?: return

        ApiClient.apiService.saveSettings(
            userId,
            currentSettings
        ).enqueue(object : Callback<UserSettings> {

            override fun onResponse(
                call: Call<UserSettings>,
                response: Response<UserSettings>
            ) {
                isLoadingSettings = false

                if (response.isSuccessful) {
                    response.body()?.let {
                        currentSettings = it
                    }

                    Toast.makeText(
                        this@SettingsActivity,
                        "Settings saved to the cloud.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@SettingsActivity,
                        "Could not create settings. HTTP ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<UserSettings>,
                t: Throwable
            ) {
                isLoadingSettings = false

                Toast.makeText(
                    this@SettingsActivity,
                    "Could not save cloud settings.",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun saveNotificationSetting(enabled: Boolean) {
        val userId = currentUserId ?: return

        currentSettings = currentSettings.copy(
            id = userId,
            userId = userId,
            notificationsEnabled = enabled
        )

        ApiClient.apiService.saveSettings(
            userId,
            currentSettings
        ).enqueue(object : Callback<UserSettings> {

            override fun onResponse(
                call: Call<UserSettings>,
                response: Response<UserSettings>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        currentSettings = it
                    }

                    Toast.makeText(
                        this@SettingsActivity,
                        "Notification setting saved.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@SettingsActivity,
                        "Cloud save failed. HTTP ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<UserSettings>,
                t: Throwable
            ) {
                Toast.makeText(
                    this@SettingsActivity,
                    "Could not sync notification setting.",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun saveLanguageSetting(language: String) {
        val userId = currentUserId ?: return

        currentSettings = currentSettings.copy(
            id = userId,
            userId = userId,
            language = language
        )

        ApiClient.apiService.saveSettings(
            userId,
            currentSettings
        ).enqueue(object : Callback<UserSettings> {

            override fun onResponse(
                call: Call<UserSettings>,
                response: Response<UserSettings>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        currentSettings = it
                    }

                    Toast.makeText(
                        this@SettingsActivity,
                        "Language preference saved to the cloud.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@SettingsActivity,
                        "Could not save language. HTTP ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<UserSettings>,
                t: Throwable
            ) {
                Toast.makeText(
                    this@SettingsActivity,
                    "Could not sync language preference.",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}

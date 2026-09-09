package com.example.medicareReminder

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var notifications: Switch
    private lateinit var reminderSound: Switch

    private lateinit var preferences:
            android.content.SharedPreferences

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_settings
        )

        preferences =
            getSharedPreferences(
                "MediCarePrefs",
                MODE_PRIVATE
            )

        val back =
            findViewById<TextView>(
                R.id.txtSettingsBack
            )

        back.setOnClickListener {
            finish()
        }

        notifications =
            findViewById(
                R.id.switchNotifications
            )

        reminderSound =
            findViewById(
                R.id.switchReminderSound
            )

        val logout =
            findViewById<Button>(
                R.id.btnLogout
            )

        val email =
            findViewById<TextView>(
                R.id.txtAccountEmail
            )

        val savedEmail =
            preferences.getString(
                "email",
                "user@example.com"
            )

        email.text =
            savedEmail

        notifications.isChecked =
            preferences.getBoolean(
                "notifications",
                true
            )

        reminderSound.isChecked =
            preferences.getBoolean(
                "reminderSound",
                true
            )

        notifications.setOnCheckedChangeListener {
                _,
                checked ->

            preferences.edit()
                .putBoolean(
                    "notifications",
                    checked
                )
                .apply()
        }

        reminderSound.setOnCheckedChangeListener {
                _,
                checked ->

            preferences.edit()
                .putBoolean(
                    "reminderSound",
                    checked
                )
                .apply()
        }

        logout.setOnClickListener {

            preferences.edit()
                .clear()
                .apply()

            val intent =
                Intent(
                    this,
                    MainActivity::class.java
                )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }
    }
}
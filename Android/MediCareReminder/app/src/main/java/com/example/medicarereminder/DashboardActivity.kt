
package com.example.medicareReminder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class DashboardActivity : AppCompatActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_dashboard
        )

        // Request notification permission
        requestNotificationPermission()

        val preferences =
            getSharedPreferences(
                "MediCarePrefs",
                MODE_PRIVATE
            )

        val fullName =
            preferences.getString(
                "fullName",
                "User"
            )

        val greeting =
            findViewById<TextView>(
                R.id.txtDashboardGreeting
            )

        greeting.text =
            "Welcome back, $fullName 👋"

        val medications =
            findViewById<LinearLayout>(
                R.id.cardMedications
            )

        val settings =
            findViewById<LinearLayout>(
                R.id.cardSettings
            )


        medications.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    MedicationsActivity::class.java
                )
            )
        }

        settings.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    SettingsActivity::class.java
                )
            )
        }
    }


    // Request notification permission on Android 13+
    private fun requestNotificationPermission() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS
                    ),
                    100
                )
            }
        }
    }
}
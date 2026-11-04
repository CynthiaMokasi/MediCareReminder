
package com.example.medicareReminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class DashboardActivity : AppCompatActivity() {

    private val channelId = "medicare_test_channel"

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            if (isGranted) {
                showTestNotification()
            } else {
                Toast.makeText(
                    this,
                    "Please allow notifications in Settings to test this feature.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)

        createNotificationChannel()

        val preferences = getSharedPreferences(
            "MediCarePrefs",
            MODE_PRIVATE
        )

        val fullName = preferences.getString(
            "fullName",
            "User"
        )

        val greeting = findViewById<TextView>(
            R.id.txtDashboardGreeting
        )

        greeting.text = "Hello and Welcome back, $fullName 👋"

        val medications = findViewById<LinearLayout>(
            R.id.cardMedications
        )

        val settings = findViewById<LinearLayout>(
            R.id.cardSettings
        )

        medications.setOnClickListener {
            startActivity(
                Intent(this, MedicationsActivity::class.java)
            )
        }

        settings.setOnClickListener {
            startActivity(
                Intent(this, SettingsActivity::class.java)
            )
        }

        val testNotification = findViewById<Button>(
            R.id.btnTestNotification
        )

        testNotification.setOnClickListener {
            checkNotificationPermission()
        }
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                channelId,
                "MediCareReminder Test Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )

            channel.description =
                "Test notifications for MediCareReminder"

            channel.enableVibration(true)

            val notificationManager =
                getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun checkNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            val permissionGranted =
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

            if (!permissionGranted) {

                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )

                return
            }
        }

        showTestNotification()
    }

    private fun showTestNotification() {

        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {

            Toast.makeText(
                this,
                "Notifications are disabled. Please enable them in Android Settings.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val notification = NotificationCompat.Builder(
            this,
            channelId
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("MediCareReminder 💊")
            .setContentText(
                "Great! Your notifications are working successfully."
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 300, 200, 300))
            .build()

        try {

            NotificationManagerCompat
                .from(this)
                .notify(9999, notification)

            Toast.makeText(
                this,
                "Test notification sent successfully!",
                Toast.LENGTH_SHORT
            ).show()

        } catch (e: SecurityException) {

            Toast.makeText(
                this,
                "Notification permission is required.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

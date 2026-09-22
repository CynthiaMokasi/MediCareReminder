package com.example.medicareReminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MedicationReminderReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        val medicationName =
            intent.getStringExtra("medicationName")
                ?: "Your medication"

        val dosage =
            intent.getStringExtra("medicationDosage")
                ?: ""

        val medicationId =
            intent.getIntExtra(
                "medicationId",
                0
            )

        val reminderTime =
            intent.getStringExtra(
                "reminderTime"
            )
                ?: ""

        val frequency =
            intent.getStringExtra(
                "medicationFrequency"
            )
                ?: ""

        // Create the notification channel
        createNotificationChannel(context)

        // Read notification settings
        val preferences =
            context.getSharedPreferences(
                "MediCarePrefs",
                Context.MODE_PRIVATE
            )

        val notificationsEnabled =
            preferences.getBoolean(
                "notifications",
                true
            )

        val soundEnabled =
            preferences.getBoolean(
                "reminderSound",
                true
            )

        // Stop if notifications are disabled
        if (!notificationsEnabled) {
            return
        }

        // Android 13+ requires notification permission
        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            if (
                context.checkSelfPermission(
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val builder =
            NotificationCompat.Builder(
                context,
                "medication_reminders"
            )
                .setSmallIcon(
                    R.mipmap.ic_launcher
                )
                .setContentTitle(
                    "💊 Medication Reminder"
                )
                .setContentText(
                    "Time to take $medicationName $dosage"
                )
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            "It is time to take your medication.\n\n" +
                                    "$medicationName\n" +
                                    "Dosage: $dosage"
                        )
                )
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setAutoCancel(true)

        if (soundEnabled) {

            builder.setDefaults(
                NotificationCompat.DEFAULT_SOUND or
                        NotificationCompat.DEFAULT_VIBRATE
            )
        }

        // Display notification
        NotificationManagerCompat
            .from(context)
            .notify(
                medicationId,
                builder.build()
            )

        // Schedule the next reminder for daily medication
        if (
            frequency.equals(
                "Every day",
                ignoreCase = true
            ) &&
            reminderTime.isNotBlank()
        ) {

            ReminderScheduler.schedule(
                context = context,
                medicationId = medicationId,
                name = medicationName,
                dosage = dosage,
                reminderTime = reminderTime,
                frequency = frequency
            )
        }
    }

    private fun createNotificationChannel(
        context: Context
    ) {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(
                    "medication_reminders",
                    "Medication Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                )

            channel.description =
                "Notifications for medication reminders"

            channel.enableVibration(true)

            val notificationManager =
                context.getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            notificationManager.createNotificationChannel(
                channel
            )
        }
    }
}
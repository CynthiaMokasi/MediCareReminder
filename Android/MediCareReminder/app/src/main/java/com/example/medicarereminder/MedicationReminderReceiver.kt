
package com.example.medicareReminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MedicationReminderReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "MEDICATION_RECEIVER"
        private const val CHANNEL_ID = "medication_reminders"
    }

    override fun onReceive(context: Context, intent: Intent) {

        val medicationName =
            intent.getStringExtra("medicationName")
                ?: "Your medication"

        val dosage =
            intent.getStringExtra("medicationDosage")
                ?: ""

        val medicationId =
            intent.getIntExtra("medicationId", 0)

        val reminderTime =
            intent.getStringExtra("reminderTime")
                ?: ""

        val frequency =
            intent.getStringExtra("medicationFrequency")
                ?: ""

        Log.d(TAG, "Reminder triggered for $medicationName")

        // Schedule the next daily reminder first.
        if (
            frequency.equals("Every day", ignoreCase = true) &&
            reminderTime.isNotBlank() &&
            medicationId != 0
        ) {
            ReminderScheduler.schedule(
                context = context,
                medicationId = medicationId,
                name = medicationName,
                dosage = dosage,
                reminderTime = reminderTime,
                frequency = frequency
            )

            Log.d(TAG, "Next daily reminder scheduled.")
        }

        // Create notification channel.
        createNotificationChannel(context)

        val preferences = context.getSharedPreferences(
            "MediCarePrefs",
            Context.MODE_PRIVATE
        )

        val notificationsEnabled =
            preferences.getBoolean("notifications", true)

        val soundEnabled =
            preferences.getBoolean("reminderSound", true)

        // Stop notification display if disabled.
        // The next reminder has already been scheduled.
        if (!notificationsEnabled) {
            Log.d(TAG, "Notifications disabled by user.")
            return
        }

        // Check Android 13+ permission.
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(TAG, "Notification permission not granted.")
            return
        }

        try {

            val builder = NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("💊 Medication Reminder")
                .setContentText(
                    "Time to take $medicationName $dosage"
                )
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            "It is time to take your medication.\n\n" +
                                    "Medication: $medicationName\n" +
                                    "Dosage: $dosage\n" +
                                    "Reminder time: $reminderTime"
                        )
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            if (soundEnabled) {
                builder.setDefaults(
                    NotificationCompat.DEFAULT_SOUND or
                            NotificationCompat.DEFAULT_VIBRATE
                )
            }

            NotificationManagerCompat
                .from(context)
                .notify(
                    medicationId,
                    builder.build()
                )

            Log.d(TAG, "Notification displayed successfully.")

        } catch (e: SecurityException) {

            Log.e(TAG, "Notification permission error.", e)

        } catch (e: Exception) {

            Log.e(TAG, "Could not display medication notification.", e)
        }
    }

    private fun createNotificationChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Medication Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {

                description =
                    "Notifications for medication reminders"

                enableVibration(true)
            }

            val notificationManager =
                context.getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }
}

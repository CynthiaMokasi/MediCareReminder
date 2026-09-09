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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MedicationReminderReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        Log.d(
            "REMINDER_TEST",
            "MedicationReminderReceiver fired!"
        )

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

        if (!notificationsEnabled) {

            Log.d(
                "REMINDER_TEST",
                "Notifications are disabled."
            )

            return
        }

        val medicationId =
            intent.getIntExtra(
                "medicationId",
                0
            )

        val medicationName =
            intent.getStringExtra(
                "medicationName"
            ) ?: "Medication"

        val dosage =
            intent.getStringExtra(
                "dosage"
            ) ?: ""

        val reminderTime =
            intent.getStringExtra(
                "reminderTime"
            ) ?: ""

        val soundEnabled =
            preferences.getBoolean(
                "reminderSound",
                true
            )

        val channelId =
            if (soundEnabled) {
                "medication_reminders_sound"
            } else {
                "medication_reminders_silent"
            }

        val notificationManager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager


        // Create notification channel
        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            if (soundEnabled) {

                val channel =
                    NotificationChannel(
                        channelId,
                        "Medication Reminders",
                        NotificationManager.IMPORTANCE_HIGH
                    )

                notificationManager.createNotificationChannel(
                    channel
                )

            } else {

                val channel =
                    NotificationChannel(
                        channelId,
                        "Silent Medication Reminders",
                        NotificationManager.IMPORTANCE_HIGH
                    )

                notificationManager.createNotificationChannel(
                    channel
                )
            }
        }


        // Check notification permission on Android 13+
        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            if (
                context.checkSelfPermission(
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                Log.e(
                    "REMINDER_TEST",
                    "Notification permission NOT granted."
                )

                return
            }
        }


        // Build notification
        val notification =
            NotificationCompat.Builder(
                context,
                channelId
            )
                .setSmallIcon(
                    android.R.drawable.ic_dialog_info
                )
                .setContentTitle(
                    "Time to take your medication"
                )
                .setContentText(
                    "$medicationName - $dosage"
                )
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setCategory(
                    NotificationCompat.CATEGORY_REMINDER
                )
                .setAutoCancel(true)
                .build()


        // Display notification
        NotificationManagerCompat
            .from(context)
            .notify(
                medicationId,
                notification
            )

        Log.d(
            "REMINDER_TEST",
            "Notification displayed for $medicationName"
        )


        // Schedule tomorrow's reminder
        if (
            medicationId != 0 &&
            reminderTime.isNotEmpty()
        ) {

            scheduleTomorrow(
                context,
                medicationId,
                medicationName,
                dosage,
                reminderTime
            )
        }
    }


    private fun scheduleTomorrow(
        context: Context,
        medicationId: Int,
        medicationName: String,
        dosage: String,
        reminderTime: String
    ) {

        try {

            val calendar =
                Calendar.getInstance()

            calendar.add(
                Calendar.DAY_OF_YEAR,
                1
            )

            val format =
                SimpleDateFormat(
                    "hh:mm a",
                    Locale.getDefault()
                )

            format.isLenient = false

            val date =
                format.parse(
                    reminderTime
                )

            if (date == null) {

                Log.e(
                    "REMINDER_TEST",
                    "Could not parse tomorrow's reminder time."
                )

                return
            }

            val timeCalendar =
                Calendar.getInstance()

            timeCalendar.time = date

            calendar.set(
                Calendar.HOUR_OF_DAY,
                timeCalendar.get(
                    Calendar.HOUR_OF_DAY
                )
            )

            calendar.set(
                Calendar.MINUTE,
                timeCalendar.get(
                    Calendar.MINUTE
                )
            )

            calendar.set(
                Calendar.SECOND,
                0
            )

            calendar.set(
                Calendar.MILLISECOND,
                0
            )


            val intent =
                Intent(
                    context,
                    MedicationReminderReceiver::class.java
                )

            intent.putExtra(
                "medicationId",
                medicationId
            )

            intent.putExtra(
                "medicationName",
                medicationName
            )

            intent.putExtra(
                "dosage",
                dosage
            )

            intent.putExtra(
                "reminderTime",
                reminderTime
            )


            val pendingIntent =
                android.app.PendingIntent.getBroadcast(
                    context,
                    medicationId,
                    intent,
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT or
                            android.app.PendingIntent.FLAG_IMMUTABLE
                )


            val alarmManager =
                context.getSystemService(
                    Context.ALARM_SERVICE
                ) as android.app.AlarmManager


            val alarmInfo =
                android.app.AlarmManager.AlarmClockInfo(
                    calendar.timeInMillis,
                    pendingIntent
                )


            alarmManager.setAlarmClock(
                alarmInfo,
                pendingIntent
            )


            Log.d(
                "REMINDER_TEST",
                "Tomorrow's reminder scheduled for ${calendar.time}"
            )

        } catch (e: Exception) {

            Log.e(
                "REMINDER_TEST",
                "Could not schedule tomorrow's reminder",
                e
            )
        }
    }
}
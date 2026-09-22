package com.example.medicareReminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.net.Uri
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object ReminderScheduler {

    fun schedule(
        context: Context,
        medicationId: Int,
        name: String,
        dosage: String,
        reminderTime: String,
        frequency: String = "Every day"
    ) {

        try {

            android.util.Log.d(
                "REMINDER_SCHEDULER",
                "Scheduling reminder: $name at $reminderTime"
            )

            val alarmManager =
                context.getSystemService(
                    Context.ALARM_SERVICE
                ) as AlarmManager

            // Check exact alarm permission on Android 12+
            if (
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.S
            ) {

                if (!alarmManager.canScheduleExactAlarms()) {

                    android.util.Log.e(
                        "REMINDER_SCHEDULER",
                        "Exact alarm permission is not granted."
                    )

                    return
                }
            }

            // Expected format:
            // 08:00 AM
            // 02:30 PM

            val format =
                SimpleDateFormat(
                    "hh:mm a",
                    Locale.getDefault()
                )

            format.isLenient = false

            val parsedTime =
                format.parse(
                    reminderTime.trim()
                        .uppercase(Locale.getDefault())
                )

            if (parsedTime == null) {

                android.util.Log.e(
                    "REMINDER_SCHEDULER",
                    "Could not parse reminder time: $reminderTime"
                )

                return
            }

            val timeCalendar =
                Calendar.getInstance()

            timeCalendar.time =
                parsedTime

            val calendar =
                Calendar.getInstance()

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

            // If today's time has already passed,
            // schedule for tomorrow.
            if (
                calendar.timeInMillis <=
                System.currentTimeMillis()
            ) {

                calendar.add(
                    Calendar.DAY_OF_YEAR,
                    1
                )
            }

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
                name
            )

            intent.putExtra(
                "medicationDosage",
                dosage
            )

            intent.putExtra(
                "reminderTime",
                reminderTime
            )

            intent.putExtra(
                "medicationFrequency",
                frequency
            )

            val pendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    medicationId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or
                            PendingIntent.FLAG_IMMUTABLE
                )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )

            android.util.Log.d(
                "REMINDER_SCHEDULER",
                "Reminder scheduled successfully for: ${calendar.time}"
            )

        } catch (e: Exception) {

            android.util.Log.e(
                "REMINDER_SCHEDULER",
                "Failed to schedule reminder",
                e
            )
        }
    }

    fun cancel(
        context: Context,
        medicationId: Int
    ) {

        val intent =
            Intent(
                context,
                MedicationReminderReceiver::class.java
            )

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                medicationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        alarmManager.cancel(
            pendingIntent
        )

        pendingIntent.cancel()

        android.util.Log.d(
            "REMINDER_SCHEDULER",
            "Reminder cancelled for medication ID: $medicationId"
        )
    }
}
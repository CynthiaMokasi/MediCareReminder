
package com.example.medicareReminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object ReminderScheduler {

    private const val TAG = "REMINDER_SCHEDULER"

    fun schedule(
        context: Context,
        medicationId: Int,
        name: String,
        dosage: String,
        reminderTime: String,
        frequency: String = "Every day"
    ) {
        try {
            val appContext = context.applicationContext

            Log.d(TAG, "Scheduling $name at $reminderTime")

            val alarmManager = appContext.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

            // Accept both 12-hour and 24-hour time formats.
            val formats = listOf(
                "hh:mm a",
                "HH:mm"
            )

            var parsedTime: Date? = null

            for (pattern in formats) {
                val formatter = SimpleDateFormat(
                    pattern,
                    Locale.getDefault()
                )

                formatter.isLenient = false

                val input = reminderTime.trim()
                    .uppercase(Locale.getDefault())

                val position = ParsePosition(0)
                val result = formatter.parse(input, position)

                if (result != null && position.index == input.length) {
                    parsedTime = result
                    break
                }
            }

            if (parsedTime == null) {
                Log.e(
                    TAG,
                    "Invalid time: $reminderTime. Use 08:00 AM or 20:00."
                )
                return
            }

            val timeCalendar = Calendar.getInstance()
            timeCalendar.time = parsedTime

            val calendar = Calendar.getInstance()

            calendar.set(
                Calendar.HOUR_OF_DAY,
                timeCalendar.get(Calendar.HOUR_OF_DAY)
            )
            calendar.set(
                Calendar.MINUTE,
                timeCalendar.get(Calendar.MINUTE)
            )
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            // If today's reminder time has passed, use tomorrow.
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            val intent = Intent(
                appContext,
                MedicationReminderReceiver::class.java
            ).apply {
                putExtra("medicationId", medicationId)
                putExtra("medicationName", name)
                putExtra("medicationDosage", dosage)
                putExtra("reminderTime", reminderTime)
                putExtra("medicationFrequency", frequency)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                appContext,
                medicationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

            // Use exact alarms when permission is available.
            if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                !alarmManager.canScheduleExactAlarms()
            ) {
                Log.w(
                    TAG,
                    "Exact alarm permission unavailable. Using inexact alarm."
                )

                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                try {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } catch (e: SecurityException) {
                    Log.w(
                        TAG,
                        "Exact alarm denied. Using inexact alarm.",
                        e
                    )

                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            }

            Log.d(
                TAG,
                "Reminder registered for ${calendar.time}"
            )

        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule reminder", e)
        }
    }

    fun cancel(context: Context, medicationId: Int) {
        val appContext = context.applicationContext

        val intent = Intent(
            appContext,
            MedicationReminderReceiver::class.java
        )

        val pendingIntent = PendingIntent.getBroadcast(
            appContext,
            medicationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = appContext.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()

        Log.d(TAG, "Cancelled reminder for medication ID: $medicationId")
    }
}

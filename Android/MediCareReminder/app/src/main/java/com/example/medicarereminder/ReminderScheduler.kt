
package com.example.medicareReminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object ReminderScheduler {

    fun schedule(
        context: Context,
        medicationId: Int,
        medicationName: String,
        dosage: String,
        reminderTime: String
    ) {

        try {

            Log.d(
                "REMINDER_TEST",
                "Scheduling $medicationName at $reminderTime"
            )

            val format = SimpleDateFormat(
                "hh:mm a",
                Locale.getDefault()
            )

            format.isLenient = false

            val date = format.parse(reminderTime)

            if (date == null) {

                Log.e(
                    "REMINDER_TEST",
                    "Could not parse reminder time: $reminderTime"
                )

                return
            }

            val timeCalendar = Calendar.getInstance()

            timeCalendar.time = date

            val calendar = Calendar.getInstance()

            calendar.set(
                Calendar.HOUR_OF_DAY,
                timeCalendar.get(Calendar.HOUR_OF_DAY)
            )

            calendar.set(
                Calendar.MINUTE,
                timeCalendar.get(Calendar.MINUTE)
            )

            calendar.set(
                Calendar.SECOND,
                0
            )

            calendar.set(
                Calendar.MILLISECOND,
                0
            )

            // If today's reminder time has already passed,
            // schedule it for tomorrow.
            if (
                calendar.timeInMillis <=
                System.currentTimeMillis()
            ) {

                calendar.add(
                    Calendar.DAY_OF_YEAR,
                    1
                )
            }

            val intent = Intent(
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

            // Cancel any previous alarm for this medication.
            alarmManager.cancel(pendingIntent)

            val alarmInfo =
                AlarmManager.AlarmClockInfo(
                    calendar.timeInMillis,
                    pendingIntent
                )

            alarmManager.setAlarmClock(
                alarmInfo,
                pendingIntent
            )

            Log.d(
                "REMINDER_TEST",
                "Alarm scheduled successfully for: ${calendar.time}"
            )

        } catch (e: Exception) {

            Log.e(
                "REMINDER_TEST",
                "Error scheduling reminder",
                e
            )
        }
    }


    fun cancel(
        context: Context,
        medicationId: Int
    ) {

        val intent = Intent(
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

        Log.d(
            "REMINDER_TEST",
            "Alarm cancelled for medication $medicationId"
        )
    }
}
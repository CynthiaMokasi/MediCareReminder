
package com.example.medicareReminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        if (
            intent?.action != Intent.ACTION_BOOT_COMPLETED &&
            intent?.action != "android.intent.action.MY_PACKAGE_REPLACED"
        ) {
            return
        }

        val pendingResult = goAsync()
        val appContext = context.applicationContext

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val database = MedicationDatabase.getDatabase(appContext)
                val medications = database.medicationDao()
                    .getMedicationsOnce(
                        appContext.getSharedPreferences(
                            "MediCarePrefs",
                            Context.MODE_PRIVATE
                        ).getInt("userId", 0)
                    )

                withContext(Dispatchers.Main) {
                    medications.forEach { medication ->

                        ReminderScheduler.schedule(
                            context = appContext,
                            medicationId = medication.medicationId,
                            name = medication.name,
                            dosage = medication.dosage,
                            reminderTime = medication.reminderTime,
                            frequency = medication.frequency
                        )
                    }

                    Log.d(
                        "BOOT_RECEIVER",
                        "Restored ${medications.size} medication reminders."
                    )
                }

            } catch (e: Exception) {
                Log.e(
                    "BOOT_RECEIVER",
                    "Could not restore medication reminders.",
                    e
                )
            } finally {
                pendingResult.finish()
            }
        }
    }
}

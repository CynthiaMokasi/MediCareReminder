
package com.example.medicareReminder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.medicareReminder.api.ApiClient
import com.example.medicareReminder.api.Medication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddMedicationActivity : AppCompatActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                Toast.makeText(
                    this,
                    "Notification permission granted.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Notifications are disabled. Enable them in Settings to receive reminders.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medication)

        requestNotificationPermission()

        val nameInput = findViewById<EditText>(R.id.etMedicationName)
        val dosageInput = findViewById<EditText>(R.id.etDosage)
        val timeInput = findViewById<EditText>(R.id.etMedicationTime)
        val frequencyInput = findViewById<EditText>(R.id.etFrequency)
        val saveButton = findViewById<Button>(R.id.btnSaveMedication)

        saveButton.setOnClickListener {

            val name = nameInput.text.toString().trim()
            val dosage = dosageInput.text.toString().trim()
            val reminderTime = timeInput.text.toString().trim()
            val frequency = frequencyInput.text.toString().trim()

            if (
                name.isBlank() ||
                dosage.isBlank() ||
                reminderTime.isBlank() ||
                frequency.isBlank()
            ) {
                Toast.makeText(
                    this,
                    "Please complete all medication fields.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val preferences = getSharedPreferences(
                "MediCarePrefs",
                MODE_PRIVATE
            )

            val userId = preferences.getInt("userId", 0)

            if (userId == 0) {
                Toast.makeText(
                    this,
                    "Please log in again.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val medication = Medication(
                medicationId = 0,
                userId = userId,
                name = name,
                dosage = dosage,
                frequency = frequency,
                reminderTime = reminderTime
            )

            saveButton.isEnabled = false

            ApiClient.apiService.addMedication(medication)
                .enqueue(object : Callback<Medication> {

                    override fun onResponse(
                        call: Call<Medication>,
                        response: Response<Medication>
                    ) {
                        if (response.isSuccessful) {

                            val saved = response.body()

                            if (saved != null) {

                                lifecycleScope.launch {

                                    try {
                                        // Save the API response into Room.
                                        withContext(Dispatchers.IO) {

                                            val database =
                                                MedicationDatabase.getDatabase(
                                                    this@AddMedicationActivity
                                                )

                                            val localMedication =
                                                MedicationEntity(
                                                    medicationId = saved.medicationId,
                                                    userId = userId,
                                                    name = saved.name,
                                                    dosage = saved.dosage,
                                                    frequency = saved.frequency,
                                                    reminderTime = saved.reminderTime,
                                                    isSynced = true
                                                )

                                            database.medicationDao()
                                                .insertMedication(localMedication)
                                        }

                                        // Schedule the medication reminder.
                                        ReminderScheduler.schedule(
                                            context = this@AddMedicationActivity,
                                            medicationId = saved.medicationId,
                                            name = saved.name,
                                            dosage = saved.dosage,
                                            reminderTime = saved.reminderTime,
                                            frequency = saved.frequency
                                        )

                                        Toast.makeText(
                                            this@AddMedicationActivity,
                                            "Medication saved locally and online. Reminder scheduled!",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        finish()

                                    } catch (e: Exception) {

                                        Log.e(
                                            "ADD_MEDICATION",
                                            "Could not save medication locally.",
                                            e
                                        )

                                        // Still schedule the first reminder.
                                        ReminderScheduler.schedule(
                                            context = this@AddMedicationActivity,
                                            medicationId = saved.medicationId,
                                            name = saved.name,
                                            dosage = saved.dosage,
                                            reminderTime = saved.reminderTime,
                                            frequency = saved.frequency
                                        )

                                        saveButton.isEnabled = true

                                        Toast.makeText(
                                            this@AddMedicationActivity,
                                            "Medication saved online, but local storage failed. Please try again.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }

                            } else {
                                saveButton.isEnabled = true

                                Toast.makeText(
                                    this@AddMedicationActivity,
                                    "Medication saved, but no response data was returned.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        } else {
                            saveButton.isEnabled = true

                            Log.e(
                                "ADD_MEDICATION",
                                "API error: ${response.code()} ${response.errorBody()?.string()}"
                            )

                            Toast.makeText(
                                this@AddMedicationActivity,
                                "Could not save medication. Error ${response.code()}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<Medication>,
                        t: Throwable
                    ) {
                        saveButton.isEnabled = true

                        Log.e(
                            "ADD_MEDICATION",
                            "Request failed",
                            t
                        )

                        Toast.makeText(
                            this@AddMedicationActivity,
                            "Connection error: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }
    }

    private fun requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }
}

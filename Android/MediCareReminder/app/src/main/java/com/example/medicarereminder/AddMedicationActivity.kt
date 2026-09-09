package com.example.medicareReminder

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.medicareReminder.api.ApiClient
import com.example.medicareReminder.api.Medication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddMedicationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_medication)

        val back =
            findViewById<TextView>(
                R.id.txtAddMedicationBack
            )

        back.setOnClickListener {
            finish()
        }

        val name =
            findViewById<EditText>(
                R.id.etMedicationName
            )

        val dosage =
            findViewById<EditText>(
                R.id.etDosage
            )

        val time =
            findViewById<EditText>(
                R.id.etMedicationTime
            )

        val frequency =
            findViewById<EditText>(
                R.id.etFrequency
            )

        val save =
            findViewById<Button>(
                R.id.btnSaveMedication
            )

        save.setOnClickListener {

            val nameText =
                name.text.toString().trim()

            val dosageText =
                dosage.text.toString().trim()

            val timeText =
                time.text.toString().trim()

            val frequencyText =
                frequency.text.toString().trim()

            when {

                nameText.isEmpty() -> {

                    name.error =
                        "Enter medication name"

                    name.requestFocus()
                }

                dosageText.isEmpty() -> {

                    dosage.error =
                        "Enter dosage"

                    dosage.requestFocus()
                }

                timeText.isEmpty() -> {

                    time.error =
                        "Enter reminder time"

                    time.requestFocus()
                }

                frequencyText.isEmpty() -> {

                    frequency.error =
                        "Enter frequency"

                    frequency.requestFocus()
                }

                else -> {

                    val preferences =
                        getSharedPreferences(
                            "MediCarePrefs",
                            MODE_PRIVATE
                        )

                    val userId =
                        preferences.getInt(
                            "userId",
                            0
                        )

                    if (userId == 0) {

                        Toast.makeText(
                            this,
                            "Please login again.",
                            Toast.LENGTH_LONG
                        ).show()

                        return@setOnClickListener
                    }

                    save.isEnabled = false
                    save.text = "Saving..."

                    val medication =
                        Medication(
                            medicationId = 0,
                            userId = userId,
                            name = nameText,
                            dosage = dosageText,
                            frequency = frequencyText,
                            reminderTime = timeText
                        )

                    ApiClient.apiService
                        .addMedication(medication)
                        .enqueue(
                            object :
                                Callback<Medication> {

                                override fun onResponse(
                                    call: Call<Medication>,
                                    response: Response<Medication>
                                ) {

                                    save.isEnabled = true
                                    save.text = "Save Medication"

                                    if (response.isSuccessful) {

                                        val saved =
                                            response.body()

                                        if (saved != null) {

                                            ReminderScheduler
                                                .schedule(
                                                    this@AddMedicationActivity,
                                                    saved.medicationId,
                                                    saved.name,
                                                    saved.dosage,
                                                    saved.reminderTime
                                                )
                                        }

                                        Toast.makeText(
                                            this@AddMedicationActivity,
                                            "Medication saved successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        finish()

                                    } else {

                                        val errorMessage = response.errorBody()?.string()

                                        android.util.Log.e(
                                            "MEDICATION_ERROR",
                                            "HTTP ${response.code()} - $errorMessage"
                                        )

                                        Toast.makeText(
                                            this@AddMedicationActivity,
                                            "Could not save medication: HTTP ${response.code()}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }

                                override fun onFailure(
                                    call: Call<Medication>,
                                    t: Throwable
                                ) {

                                    save.isEnabled = true
                                    save.text = "Save Medication"

                                    Toast.makeText(
                                        this@AddMedicationActivity,
                                        "API connection failed: ${t.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        )
                }
            }
        }
    }
}
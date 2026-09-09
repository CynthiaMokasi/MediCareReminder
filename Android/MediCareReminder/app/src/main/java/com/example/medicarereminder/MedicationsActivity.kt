package com.example.medicareReminder

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.medicareReminder.api.ApiClient
import com.example.medicareReminder.api.Medication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MedicationsActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout
    private lateinit var status: TextView

    private var userId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_medications)

        val back =
            findViewById<TextView>(
                R.id.txtMedicationBack
            )

        back.setOnClickListener {
            finish()
        }

        container =
            findViewById(
                R.id.medicationsContainer
            )

        status =
            findViewById(
                R.id.txtMedicationStatus
            )

        val addMedication =
            findViewById<Button>(
                R.id.btnAddMedication
            )

        addMedication.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AddMedicationActivity::class.java
                )
            )
        }

        val preferences =
            getSharedPreferences(
                "MediCarePrefs",
                MODE_PRIVATE
            )

        userId =
            preferences.getInt(
                "userId",
                0
            )
    }

    override fun onResume() {
        super.onResume()

        if (userId != 0) {
            loadMedications()
        }
    }

    private fun loadMedications() {

        status.text = "Loading medications..."

        ApiClient.apiService
            .getMedications(userId)
            .enqueue(
                object :
                    Callback<List<Medication>> {

                    override fun onResponse(
                        call: Call<List<Medication>>,
                        response: Response<List<Medication>>
                    ) {

                        if (response.isSuccessful) {

                            val medications =
                                response.body()
                                    ?: emptyList()

                            displayMedications(
                                medications
                            )

                        } else {

                            status.text =
                                "Could not load medications."
                        }
                    }

                    override fun onFailure(
                        call: Call<List<Medication>>,
                        t: Throwable
                    ) {

                        status.text =
                            "Could not connect to API."

                        Toast.makeText(
                            this@MedicationsActivity,
                            t.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }

    private fun displayMedications(
        medications: List<Medication>
    ) {

        container.removeAllViews()

        if (medications.isEmpty()) {

            status.text =
                "You have no medications yet."

            return
        }

        status.text =
            "${medications.size} medication(s)"

        for (medication in medications) {

            val card =
                LinearLayout(this)

            card.orientation =
                LinearLayout.VERTICAL

            card.setPadding(
                40,
                30,
                40,
                30
            )

            card.background =
                getDrawable(
                    R.drawable.bg_white_card
                )

            val params =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            params.setMargins(
                0,
                20,
                0,
                0
            )

            card.layoutParams = params

            val title =
                TextView(this)

            title.text =
                medication.name

            title.textSize =
                19f

            title.setTextColor(
                getColor(
                    R.color.dark_navy
                )
            )

            title.setTypeface(
                null,
                android.graphics.Typeface.BOLD
            )

            card.addView(title)

            val details =
                TextView(this)

            details.text =
                "${medication.dosage} • ${medication.frequency}"

            details.textSize =
                14f

            details.setTextColor(
                getColor(
                    R.color.text_secondary
                )
            )

            val detailsParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            detailsParams.topMargin = 8

            details.layoutParams =
                detailsParams

            card.addView(details)

            val reminder =
                TextView(this)

            reminder.text =
                "Reminder: ${medication.reminderTime}"

            reminder.textSize =
                14f

            reminder.setTextColor(
                getColor(
                    R.color.primary_blue
                )
            )

            reminder.setTypeface(
                null,
                android.graphics.Typeface.BOLD
            )

            val reminderParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            reminderParams.topMargin = 10

            reminder.layoutParams =
                reminderParams

            card.addView(reminder)

            val buttons =
                LinearLayout(this)

            buttons.orientation =
                LinearLayout.HORIZONTAL

            val edit =
                Button(this)

            edit.text =
                "Edit"

            edit.setOnClickListener {

                showEditDialog(
                    medication
                )
            }

            val delete =
                Button(this)

            delete.text =
                "Delete"

            delete.setOnClickListener {

                deleteMedication(
                    medication
                )
            }

            buttons.addView(
                edit,
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            )

            buttons.addView(
                delete,
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            )

            val buttonParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            buttonParams.topMargin = 15

            buttons.layoutParams =
                buttonParams

            card.addView(buttons)

            container.addView(card)
        }
    }

    private fun showEditDialog(
        medication: Medication
    ) {

        val layout =
            LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(
            40,
            10,
            40,
            10
        )

        val name =
            EditText(this)

        name.hint =
            "Medication Name"

        name.setText(
            medication.name
        )

        layout.addView(name)

        val dosage =
            EditText(this)

        dosage.hint =
            "Dosage"

        dosage.setText(
            medication.dosage
        )

        layout.addView(dosage)

        val frequency =
            EditText(this)

        frequency.hint =
            "Frequency"

        frequency.setText(
            medication.frequency
        )

        layout.addView(frequency)

        val time =
            EditText(this)

        time.hint =
            "Reminder Time e.g. 08:00 AM"

        time.setText(
            medication.reminderTime
        )

        layout.addView(time)

        val dialog =
            AlertDialog.Builder(this)
                .setTitle("Edit Medication")
                .setView(layout)
                .setNegativeButton(
                    "Cancel",
                    null
                )
                .setPositiveButton(
                    "Save",
                    null
                )
                .create()

        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val updated =
                    medication.copy(
                        name =
                            name.text.toString().trim(),
                        dosage =
                            dosage.text.toString().trim(),
                        frequency =
                            frequency.text.toString().trim(),
                        reminderTime =
                            time.text.toString().trim()
                    )

                updateMedication(
                    updated
                )

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun updateMedication(
        medication: Medication
    ) {

        ApiClient.apiService
            .updateMedication(
                medication.medicationId,
                medication
            )
            .enqueue(
                object :
                    Callback<Medication> {

                    override fun onResponse(
                        call: Call<Medication>,
                        response: Response<Medication>
                    ) {

                        if (response.isSuccessful) {

                            val updated =
                                response.body()

                            if (updated != null) {

                                ReminderScheduler
                                    .schedule(
                                        this@MedicationsActivity,
                                        updated.medicationId,
                                        updated.name,
                                        updated.dosage,
                                        updated.reminderTime
                                    )
                            }

                            Toast.makeText(
                                this@MedicationsActivity,
                                "Medication updated.",
                                Toast.LENGTH_SHORT
                            ).show()

                            loadMedications()

                        } else {

                            Toast.makeText(
                                this@MedicationsActivity,
                                "Could not update medication.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<Medication>,
                        t: Throwable
                    ) {

                        Toast.makeText(
                            this@MedicationsActivity,
                            "API error: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }

    private fun deleteMedication(
        medication: Medication
    ) {

        AlertDialog.Builder(this)
            .setTitle("Delete Medication")
            .setMessage(
                "Are you sure you want to delete ${medication.name}?"
            )
            .setNegativeButton(
                "Cancel",
                null
            )
            .setPositiveButton(
                "Delete"
            ) { _, _ ->

                ApiClient.apiService
                    .deleteMedication(
                        medication.medicationId
                    )
                    .enqueue(
                        object :
                            Callback<Void> {

                            override fun onResponse(
                                call: Call<Void>,
                                response: Response<Void>
                            ) {

                                if (response.isSuccessful) {

                                    ReminderScheduler
                                        .cancel(
                                            this@MedicationsActivity,
                                            medication.medicationId
                                        )

                                    Toast.makeText(
                                        this@MedicationsActivity,
                                        "Medication deleted.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    loadMedications()

                                } else {

                                    Toast.makeText(
                                        this@MedicationsActivity,
                                        "Could not delete medication.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                            override fun onFailure(
                                call: Call<Void>,
                                t: Throwable
                            ) {

                                Toast.makeText(
                                    this@MedicationsActivity,
                                    "API error: ${t.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    )
            }
            .show()
    }
}
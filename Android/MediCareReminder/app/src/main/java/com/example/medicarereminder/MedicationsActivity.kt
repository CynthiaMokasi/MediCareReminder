
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.medicareReminder.api.ApiClient
import com.example.medicareReminder.api.Medication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MedicationsActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout
    private lateinit var status: TextView
    private lateinit var medicationDao: MedicationDao

    private var userId: Int = 0
    private var apiMessage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_medications)

        val back = findViewById<TextView>(R.id.txtMedicationBack)
        back.setOnClickListener {
            finish()
        }

        container = findViewById(R.id.medicationsContainer)
        status = findViewById(R.id.txtMedicationStatus)

        val addMedication = findViewById<Button>(R.id.btnAddMedication)

        addMedication.setOnClickListener {
            startActivity(
                Intent(this, AddMedicationActivity::class.java)
            )
        }

        val preferences = getSharedPreferences(
            "MediCarePrefs",
            MODE_PRIVATE
        )

        userId = preferences.getInt("userId", 0)

        medicationDao = MedicationDatabase
            .getDatabase(this)
            .medicationDao()

        if (userId == 0) {
            status.text = "Please log in to view your medications."
            return
        }

        // Observe local Room database.
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                medicationDao.getMedications(userId).collect { cachedMedications ->

                    if (cachedMedications.isNotEmpty()) {

                        displayMedications(
                            cachedMedications.map { it.toMedication() }
                        )

                        if (apiMessage != null) {
                            status.text = apiMessage
                        } else {
                            status.text =
                                "${cachedMedications.size} medication(s) saved locally."
                        }

                    } else if (apiMessage != null) {

                        status.text = apiMessage

                    } else {

                        status.text = "No medications saved yet."
                        container.removeAllViews()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (userId != 0) {
            loadMedications()
        }
    }

    // Convert API medication into Room entity.
    private fun Medication.toEntity(): MedicationEntity {

        return MedicationEntity(
            medicationId = this.medicationId,
            userId = this@MedicationsActivity.userId,
            name = this.name,
            dosage = this.dosage,
            frequency = this.frequency,
            reminderTime = this.reminderTime,
            isSynced = true
        )
    }

    // Convert Room entity into API medication model.
    private fun MedicationEntity.toMedication(): Medication {

        return Medication(
            medicationId = medicationId,
            userId = userId,
            name = name,
            dosage = dosage,
            frequency = frequency,
            reminderTime = reminderTime
        )
    }

    // Load online medications and save them locally.
    private fun loadMedications() {

        apiMessage = "Loading medications..."
        status.text = apiMessage

        ApiClient.apiService
            .getMedications(userId)
            .enqueue(object : Callback<List<Medication>> {

                override fun onResponse(
                    call: Call<List<Medication>>,
                    response: Response<List<Medication>>
                ) {

                    if (response.isSuccessful) {

                        val medications = response.body() ?: emptyList()

                        lifecycleScope.launch {

                            try {

                                val savedCount = withContext(Dispatchers.IO) {

                                    if (medications.isNotEmpty()) {

                                        val localRecords = medications.map {
                                            it.toEntity()
                                        }

                                        medicationDao.insertMedications(
                                            localRecords
                                        )
                                    }

                                    medicationDao.getMedicationCount(userId)
                                }

                                apiMessage = null

                                status.text =
                                    "$savedCount medication(s) stored locally."

                                if (savedCount > 0) {

                                    Toast.makeText(
                                        this@MedicationsActivity,
                                        "$savedCount medication(s) saved to your offline database.",
                                        Toast.LENGTH_LONG
                                    ).show()

                                } else {

                                    Toast.makeText(
                                        this@MedicationsActivity,
                                        "No medications were returned by the server.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            } catch (e: Exception) {

                                apiMessage =
                                    "Could not save medications locally."

                                status.text = apiMessage

                                Toast.makeText(
                                    this@MedicationsActivity,
                                    "Room database error: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                    } else {

                        showOfflineMedications(
                            "Server unavailable. Showing saved medications."
                        )
                    }
                }

                override fun onFailure(
                    call: Call<List<Medication>>,
                    t: Throwable
                ) {

                    showOfflineMedications(
                        "Offline mode: displaying saved medications."
                    )
                }
            })
    }

    // Retrieve medications directly from Room when the API is unavailable.
    private fun showOfflineMedications(message: String) {

        apiMessage = message

        lifecycleScope.launch {

            try {

                val cachedMedications = withContext(Dispatchers.IO) {
                    medicationDao.getMedicationsOnce(userId)
                }

                if (cachedMedications.isNotEmpty()) {

                    displayMedications(
                        cachedMedications.map { it.toMedication() }
                    )

                    status.text =
                        "${cachedMedications.size} medication(s) available offline."

                    Toast.makeText(
                        this@MedicationsActivity,
                        "${cachedMedications.size} saved medications loaded offline.",
                        Toast.LENGTH_LONG
                    ).show()

                } else {

                    container.removeAllViews()
                    status.text = "No saved medications available offline."
                }

            } catch (e: Exception) {

                container.removeAllViews()
                status.text = "Could not retrieve saved medications."

                Toast.makeText(
                    this@MedicationsActivity,
                    "Offline database error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // Display medication cards.
    private fun displayMedications(
        medications: List<Medication>
    ) {

        container.removeAllViews()

        if (medications.isEmpty()) {
            status.text = "You have no medications yet."
            return
        }

        for (medication in medications) {

            val card = LinearLayout(this)
            card.orientation = LinearLayout.VERTICAL

            card.setPadding(40, 30, 40, 30)

            card.background = getDrawable(
                R.drawable.bg_white_card
            )

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.setMargins(0, 20, 0, 0)
            card.layoutParams = params

            val title = TextView(this)
            title.text = medication.name
            title.textSize = 19f

            title.setTextColor(
                getColor(R.color.dark_navy)
            )

            title.setTypeface(
                null,
                android.graphics.Typeface.BOLD
            )

            card.addView(title)

            val details = TextView(this)
            details.text =
                "${medication.dosage} • ${medication.frequency}"

            details.textSize = 14f

            details.setTextColor(
                getColor(R.color.text_secondary)
            )

            val detailsParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            detailsParams.topMargin = 8
            details.layoutParams = detailsParams

            card.addView(details)

            val reminder = TextView(this)
            reminder.text =
                "Reminder: ${medication.reminderTime}"

            reminder.textSize = 14f

            reminder.setTextColor(
                getColor(R.color.primary_blue)
            )

            reminder.setTypeface(
                null,
                android.graphics.Typeface.BOLD
            )

            val reminderParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            reminderParams.topMargin = 10
            reminder.layoutParams = reminderParams

            card.addView(reminder)

            val buttons = LinearLayout(this)
            buttons.orientation = LinearLayout.HORIZONTAL

            val edit = Button(this)
            edit.text = "Edit"

            edit.setOnClickListener {
                showEditDialog(medication)
            }

            val delete = Button(this)
            delete.text = "Delete"

            delete.setTextColor(
                android.graphics.Color.rgb(180, 50, 50)
            )

            delete.setBackgroundColor(
                android.graphics.Color.rgb(255, 220, 220)
            )

            delete.setOnClickListener {
                deleteMedication(medication)
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

            val buttonParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            buttonParams.topMargin = 15
            buttons.layoutParams = buttonParams

            card.addView(buttons)

            container.addView(card)
        }
    }

    // Edit medication details.
    private fun showEditDialog(
        medication: Medication
    ) {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 10, 40, 10)

        val name = EditText(this)
        name.hint = "Medication Name"
        name.setText(medication.name)
        layout.addView(name)

        val dosage = EditText(this)
        dosage.hint = "Dosage"
        dosage.setText(medication.dosage)
        layout.addView(dosage)

        val frequency = EditText(this)
        frequency.hint = "Frequency"
        frequency.setText(medication.frequency)
        layout.addView(frequency)

        val time = EditText(this)
        time.hint = "Reminder Time e.g. 08:00 AM"
        time.setText(medication.reminderTime)
        layout.addView(time)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Edit Medication")
            .setView(layout)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save", null)
            .create()

        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val updated = medication.copy(
                    name = name.text.toString().trim(),
                    dosage = dosage.text.toString().trim(),
                    frequency = frequency.text.toString().trim(),
                    reminderTime = time.text.toString().trim()
                )

                if (
                    updated.name.isBlank() ||
                    updated.dosage.isBlank() ||
                    updated.frequency.isBlank() ||
                    updated.reminderTime.isBlank()
                ) {

                    Toast.makeText(
                        this,
                        "Please complete all fields.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                updateMedication(updated)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    // Update medication online and refresh Room.
    private fun updateMedication(
        medication: Medication
    ) {

        ApiClient.apiService
            .updateMedication(
                medication.medicationId,
                medication
            )
            .enqueue(object : Callback<Medication> {

                override fun onResponse(
                    call: Call<Medication>,
                    response: Response<Medication>
                ) {

                    if (response.isSuccessful) {

                        val updated = response.body() ?: medication

                        lifecycleScope.launch {

                            withContext(Dispatchers.IO) {
                                medicationDao.insertMedication(
                                    updated.toEntity()
                                )
                            }
                        }

                        ReminderScheduler.schedule(
                            this@MedicationsActivity,
                            updated.medicationId,
                            updated.name,
                            updated.dosage,
                            updated.reminderTime
                        )

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
            })
    }

    // Delete medication online and remove it from Room.
    private fun deleteMedication(
        medication: Medication
    ) {

        AlertDialog.Builder(this)
            .setTitle("Delete Medication")
            .setMessage(
                "Are you sure you want to delete ${medication.name}?"
            )
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->

                ApiClient.apiService
                    .deleteMedication(medication.medicationId)
                    .enqueue(object : Callback<Void> {

                        override fun onResponse(
                            call: Call<Void>,
                            response: Response<Void>
                        ) {

                            if (response.isSuccessful) {

                                ReminderScheduler.cancel(
                                    this@MedicationsActivity,
                                    medication.medicationId
                                )

                                lifecycleScope.launch {

                                    withContext(Dispatchers.IO) {
                                        medicationDao.deleteMedication(
                                            medication.medicationId
                                        )
                                    }
                                }

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
                    })
            }
            .show()
    }
}

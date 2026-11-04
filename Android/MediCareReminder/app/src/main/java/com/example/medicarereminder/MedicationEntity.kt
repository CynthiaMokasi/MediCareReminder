
package com.example.medicareReminder

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class MedicationEntity(

    @PrimaryKey
    val medicationId: Int,

    val userId: Int,
    val name: String,
    val dosage: String,
    val frequency: String,
    val reminderTime: String,

    val isSynced: Boolean = true
)

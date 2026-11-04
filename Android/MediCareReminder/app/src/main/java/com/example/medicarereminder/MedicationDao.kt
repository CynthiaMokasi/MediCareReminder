
package com.example.medicareReminder

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {

    @Query("SELECT * FROM medications WHERE userId = :userId")
    fun getMedications(userId: Int): Flow<List<MedicationEntity>>

    @Query("SELECT * FROM medications WHERE userId = :userId")
    suspend fun getMedicationsOnce(userId: Int): List<MedicationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: MedicationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedications(medications: List<MedicationEntity>)

    @Query("DELETE FROM medications WHERE medicationId = :medicationId")
    suspend fun deleteMedication(medicationId: Int)

    @Query("DELETE FROM medications WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: Int)

    @Query("SELECT * FROM medications WHERE isSynced = 0")
    suspend fun getUnsyncedMedications(): List<MedicationEntity>

    @Query("SELECT COUNT(*) FROM medications WHERE userId = :userId")
    suspend fun getMedicationCount(userId: Int): Int
}

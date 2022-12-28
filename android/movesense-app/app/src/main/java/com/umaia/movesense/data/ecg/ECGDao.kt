package com.umaia.movesense.data.ecg

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ECGDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addHr(ecg: ECG)

    @Query("SELECT * FROM ecg_table ORDER BY id ASC")
    fun readAllECG(): LiveData<List<ECG>>

    @Query("SELECT * FROM ecg_table WHERE id !=(SELECT MAX(id) FROM GYRO_TABLE) ORDER BY ID")
    fun getAllECG(): LiveData<List<ECG>>

    //@Query("DELETE FROM acc_table WHERE id in (SELECT id from acc_table limit :id)")
    @Query("DELETE FROM ecg_table WHERE id <(SELECT MAX(id) FROM GYRO_TABLE)")
    fun deleteAll()

    @Query("DELETE FROM ecg_table WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT id from ecg_table ORDER BY id DESC LIMIT 1")
    suspend fun getIdFromLastRecord() : Long

}
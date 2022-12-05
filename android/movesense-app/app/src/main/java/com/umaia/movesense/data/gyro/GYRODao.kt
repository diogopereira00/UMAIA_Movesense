package com.umaia.movesense.data.gyro

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.umaia.movesense.data.gyro.GYRO

@Dao
interface GYRODao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addGYRO(gyro: GYRO)

    @Query("SELECT * FROM gyro_table ORDER BY id ASC")
    fun readAllGyro(): LiveData<List<GYRO>>
}
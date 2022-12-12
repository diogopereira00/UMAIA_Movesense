package com.umaia.movesense.data.gyro

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.umaia.movesense.data.acc.ACC
import com.umaia.movesense.data.gyro.GYRO

@Dao
interface GYRODao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addGYRO(gyro: GYRO)

    @Query("SELECT * FROM gyro_table ORDER BY id ASC")
    fun readAllGyro(): LiveData<List<GYRO>>


    @Query("SELECT * FROM GYRO_TABLE WHERE id !=(SELECT MAX(id) FROM ACC_TABLE) ORDER BY ID")
    fun getAllGyro(): LiveData<List<GYRO>>

    //@Query("DELETE FROM acc_table WHERE id in (SELECT id from acc_table limit :id)")
    @Query("DELETE FROM GYRO_TABLE WHERE id <(SELECT MAX(id) FROM ACC_TABLE)")
    fun deleteAll()

    @Query("DELETE FROM gyro_table WHERE id = :id")
    suspend fun deleteById(id: Long)
}
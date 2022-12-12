package com.umaia.movesense.data.magn

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MAGNDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMAGNO(magn: MAGN)

    @Query("SELECT * FROM magn_table ORDER BY id ASC")
    fun readAllMAGN(): LiveData<List<MAGN>>

    @Query("SELECT * FROM magn_table WHERE id !=(SELECT MAX(id) FROM ACC_TABLE) ORDER BY ID")
    fun getAllMagn(): LiveData<List<MAGN>>

    //@Query("DELETE FROM acc_table WHERE id in (SELECT id from acc_table limit :id)")
    @Query("DELETE FROM magn_table WHERE id <(SELECT MAX(id) FROM ACC_TABLE)")
    fun deleteAll()

    @Query("DELETE FROM gyro_table WHERE id = :id")
    suspend fun deleteById(id: Long)
}
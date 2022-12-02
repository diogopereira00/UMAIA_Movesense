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
}
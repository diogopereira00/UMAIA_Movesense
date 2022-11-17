package com.umaia.movesense.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HrDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addHr(hr: Hr)

    @Query("SELECT * FROM hr_table ORDER BY id ASC")
    fun readAllHr(): LiveData<List<Hr>>
}
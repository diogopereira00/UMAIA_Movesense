package com.umaia.movesense.data.acc

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.umaia.movesense.data.acc.ACC

@Dao
interface ACCDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addACC(acc: ACC)

    @Query("SELECT * FROM acc_table ORDER BY id ASC")
    fun readAllACC(): LiveData<List<ACC>>
}
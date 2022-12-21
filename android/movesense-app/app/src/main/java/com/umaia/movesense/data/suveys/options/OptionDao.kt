package com.umaia.movesense.data.suveys.options

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.umaia.movesense.data.suveys.questions.Question

@Dao
interface OptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addOption(option: Option)

}
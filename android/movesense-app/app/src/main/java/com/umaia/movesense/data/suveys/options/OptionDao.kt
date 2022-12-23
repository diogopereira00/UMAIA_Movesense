package com.umaia.movesense.data.suveys.options

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.umaia.movesense.data.acc.ACC
import com.umaia.movesense.data.suveys.questions.Question

@Dao
interface OptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addOption(option: Option)

    @Query("SELECT * from options_table WHERE id = :questionId ORDER  BY id ")
    suspend fun getOptionByQuestionID(questionId: Long): List<Option>

    @Query("SELECT text from options_table WHERE id = :optionID ORDER  BY id ")
    suspend fun getOptionTextById(optionID: Long): String
}
package com.umaia.movesense.data.suveys.options

import androidx.lifecycle.LiveData
import androidx.room.*
import com.umaia.movesense.data.acc.ACC
import com.umaia.movesense.data.suveys.answers.Answer
import com.umaia.movesense.data.suveys.questions.Question
import kotlinx.coroutines.flow.Flow

@Dao
interface OptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addOption(option: Option)

//    @Query("SELECT * from options_table WHERE id = :questionId ORDER  BY id ")
//    suspend fun getOptionByQuestionID(questionId: Long): List<Option>

    @Query("SELECT text from options_table WHERE id = :optionID ORDER  BY id ")
    suspend fun getOptionTextById(optionID: Long): String

    @Query("SELECT * from options_table where id = :optionID ORDER BY id")
    fun getOptionByID(optionID: Long) : Flow<Option>

    @Transaction
    @Query("SELECT options_table.*, question_id FROM options_table " +
            "INNER JOIN questions_options_table ON options_table.id = questions_options_table.option_id " +
            "WHERE questions_options_table.question_id = :questionId")
    fun getOptionByQuestionID(questionId: String): Flow<List<Option>>


    @Transaction
    @Query("SELECT * FROM options_table")
    fun getAllOptions(): LiveData<List<Option>>




}
package com.umaia.movesense.data.suveys.questions_options

import androidx.room.*
import com.umaia.movesense.data.suveys.options.Option
import com.umaia.movesense.data.suveys.questions.Question
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionOptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addQuestionOption(questionOption: QuestionOption)

    @Transaction
    @Query("SELECT * FROM questions_options_table where question_id = :id")
    fun getQuestionsBySectionID(id: String) : Flow<List<QuestionOption>>
}
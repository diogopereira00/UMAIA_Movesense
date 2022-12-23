package com.umaia.movesense.data.suveys.questions_options

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.umaia.movesense.data.suveys.options.Option

@Dao
interface QuestionOptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addQuestionOption(questionOption: QuestionOption)


}
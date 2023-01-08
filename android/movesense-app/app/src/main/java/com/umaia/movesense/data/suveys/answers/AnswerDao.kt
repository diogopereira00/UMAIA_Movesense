package com.umaia.movesense.data.suveys.answers

import androidx.room.*
import com.umaia.movesense.data.suveys.options.Option
import com.umaia.movesense.data.suveys.studies.Study
import kotlinx.coroutines.flow.Flow

@Dao
interface AnswerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAnswer(answer: Answer)

    @Transaction
    @Query("SELECT * FROM answers_table")
    fun getAllAnswers(): Flow<List<Answer>>
}
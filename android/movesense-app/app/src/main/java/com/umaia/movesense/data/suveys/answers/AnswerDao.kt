package com.umaia.movesense.data.suveys.answers

import androidx.lifecycle.LiveData
import androidx.room.*
import com.umaia.movesense.data.suveys.options.Option
import com.umaia.movesense.data.suveys.studies.Study
import com.umaia.movesense.data.temp.TEMP
import kotlinx.coroutines.flow.Flow

@Dao
interface AnswerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAnswer(answer: Answer)

    @Transaction
    @Query("SELECT * FROM answers_table")
    fun getAllAnswers(): LiveData<List<Answer>>


    @Query("DELETE FROM answers_table WHERE id <(SELECT MAX(id) FROM answers_table)")
    fun deleteAll()

    @Query("DELETE FROM answers_table WHERE id = :id")
    fun deleteById(id: Long)
}
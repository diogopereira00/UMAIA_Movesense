package com.umaia.movesense.data.suveys.questions

import androidx.room.*
import com.umaia.movesense.data.suveys.sections.Section
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addQuestion(question: Question)


    @Transaction
    @Query("SELECT * FROM questions_table where section_id = :id")
    fun getQuestionsBySectionID(id: String) : Flow<List<Question>>
}
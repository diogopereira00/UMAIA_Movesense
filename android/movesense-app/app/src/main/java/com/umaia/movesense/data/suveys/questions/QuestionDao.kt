package com.umaia.movesense.data.suveys.questions

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.umaia.movesense.data.suveys.sections.Section

@Dao
interface QuestionDao {
    @Insert
    suspend fun addQuestion(question: Question)

}
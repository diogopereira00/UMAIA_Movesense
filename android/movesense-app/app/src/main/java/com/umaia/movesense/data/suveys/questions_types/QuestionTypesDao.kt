package com.umaia.movesense.data.suveys.questions_types

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface QuestionTypesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTypes(questionTypes: QuestionTypes)

}
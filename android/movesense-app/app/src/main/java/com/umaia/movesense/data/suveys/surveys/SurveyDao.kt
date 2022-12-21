package com.umaia.movesense.data.suveys.surveys

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.umaia.movesense.data.suveys.studies.Study

@Dao
interface SurveyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSurvey(survey: Survey)

}
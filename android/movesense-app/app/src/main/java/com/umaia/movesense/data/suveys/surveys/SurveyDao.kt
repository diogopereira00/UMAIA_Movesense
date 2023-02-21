package com.umaia.movesense.data.suveys.surveys

import androidx.lifecycle.LiveData
import androidx.room.*
import com.umaia.movesense.data.suveys.relations.FullSurvey
import com.umaia.movesense.data.suveys.studies.Study
import kotlinx.coroutines.flow.Flow

@Dao
interface SurveyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSurvey(survey: Survey)


    @Transaction
    @Query("SELECT * FROM surveys_table where id = :id")
    fun getSurveyByID(id: String) : Flow<Survey>


    @Transaction
    @Query("SELECT * FROM surveys_table")
    fun getSurveysWithSections(): LiveData<List<FullSurvey>>

}
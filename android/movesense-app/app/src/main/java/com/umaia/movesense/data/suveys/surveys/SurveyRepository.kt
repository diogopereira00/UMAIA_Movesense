package com.umaia.movesense.data.suveys.surveys

import androidx.lifecycle.LiveData
import com.umaia.movesense.data.suveys.relations.FullSurvey
import com.umaia.movesense.data.suveys.studies.Study
import com.umaia.movesense.data.suveys.studies.StudyDao


import com.umaia.movesense.data.suveys.surveys.SurveyDao
import kotlinx.coroutines.flow.Flow

class SurveyRepository(private val surveyDao: SurveyDao) {


//    val getAllHr: LiveData<List<Hr>> = studyDao.addStudy()
//    suspend fun deleteByID(id: Long){
//        hrDao.deleteById(id)
//    }

    suspend fun add(survey: Survey){
        surveyDao.addSurvey(survey)
    }

    fun getSurveyByID(id: String) : Flow<Survey> {
        return surveyDao.getSurveyByID(id)
    }

    fun getFullSurvey(): LiveData<List<FullSurvey>> {
        return surveyDao.getSurveysWithSections()
    }

}
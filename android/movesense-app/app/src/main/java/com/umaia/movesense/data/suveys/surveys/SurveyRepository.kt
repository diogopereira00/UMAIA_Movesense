package com.umaia.movesense.data.suveys.surveys

import com.umaia.movesense.data.suveys.studies.Study
import com.umaia.movesense.data.suveys.studies.StudyDao


import com.umaia.movesense.data.suveys.surveys.SurveyDao

class SurveyRepository(private val surveyDao: SurveyDao) {


//    val getAllHr: LiveData<List<Hr>> = studyDao.addStudy()
//    suspend fun deleteByID(id: Long){
//        hrDao.deleteById(id)
//    }

    suspend fun add(survey: Survey){
        surveyDao.addSurvey(survey)
    }
}
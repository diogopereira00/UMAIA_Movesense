package com.umaia.movesense.data.suveys.user_surveys

import com.umaia.movesense.data.suveys.answers.Answer
import kotlinx.coroutines.flow.Flow


class UserSurveysRepository(private val userSurveysDao: UserSurveysDao) {


//    val getAllHr: LiveData<List<Hr>> = studyDao.addStudy()
//    suspend fun deleteByID(id: Long){
//        hrDao.deleteById(id)
//    }

     suspend fun add(userSurveys: UserSurveys) : Long{
        return userSurveysDao.addUserSurvey(userSurveys)
    }
    suspend fun getIdFromLastRecord() : Long {
        return userSurveysDao.getIdFromLastRecord()
    }
}
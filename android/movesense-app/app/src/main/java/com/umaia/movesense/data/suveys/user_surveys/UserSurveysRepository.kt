package com.umaia.movesense.data.suveys.user_surveys



class UserSurveysRepository(private val userSurveysDao: UserSurveysDao) {


//    val getAllHr: LiveData<List<Hr>> = studyDao.addStudy()
//    suspend fun deleteByID(id: Long){
//        hrDao.deleteById(id)
//    }

    suspend fun add(userSurveys: UserSurveys){
        userSurveysDao.addUserSurvey(userSurveys)
    }
}
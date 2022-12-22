package com.umaia.movesense.data.suveys.user_studies

class UserStudiesRepository(private val userStudiesDao: UserStudiesDao) {


//    val getAllHr: LiveData<List<Hr>> = studyDao.addStudy()
//    suspend fun deleteByID(id: Long){
//        hrDao.deleteById(id)
//    }

    suspend fun add(userStudies: UserStudies){
        userStudiesDao.addUserStudy(userStudies)
    }
}
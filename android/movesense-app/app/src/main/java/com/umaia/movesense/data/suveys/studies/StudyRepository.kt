package com.umaia.movesense.data.suveys.studies

import kotlinx.coroutines.flow.Flow


class StudyRepository(private val studyDao: StudyDao) {

    suspend fun add(study: Study){
        studyDao.addStudy(study)
    }

    suspend fun  getStudyVersionById(id: String) : Double{
        return studyDao.getVersionByID(id)
    }
    suspend fun  getAdminPasswordStudy(id: String) : String{
        return studyDao.getAdminPassword(id)
    }

}
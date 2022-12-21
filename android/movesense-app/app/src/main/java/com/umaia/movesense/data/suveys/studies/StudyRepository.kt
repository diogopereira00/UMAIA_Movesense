package com.umaia.movesense.data.suveys.studies


class StudyRepository(private val studyDao: StudyDao) {

    suspend fun add(study: Study){
        studyDao.addStudy(study)
    }
}
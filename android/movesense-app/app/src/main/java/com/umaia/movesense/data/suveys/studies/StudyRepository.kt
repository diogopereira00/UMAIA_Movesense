package com.umaia.movesense.data.suveys.studies


class StudyRepository(private val studyDao: StudyDao) {

    suspend fun add(study: Study){
        studyDao.addStudy(study)
    }

    suspend fun  getStudyVersionById(id: String) : Double{
        return studyDao.getVersionByID(id)
    }
}
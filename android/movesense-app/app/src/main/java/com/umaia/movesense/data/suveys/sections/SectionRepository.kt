package com.umaia.movesense.data.suveys.sections

import com.umaia.movesense.data.suveys.studies.Study
import com.umaia.movesense.data.suveys.studies.StudyDao


class SectionRepository(private val sectionDao: SectionDao) {

    suspend fun add(section: Section){
        sectionDao.addSection(section)
    }
}
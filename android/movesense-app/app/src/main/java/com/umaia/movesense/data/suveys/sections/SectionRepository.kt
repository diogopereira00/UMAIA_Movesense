package com.umaia.movesense.data.suveys.sections

import com.umaia.movesense.data.suveys.studies.Study
import com.umaia.movesense.data.suveys.studies.StudyDao
import com.umaia.movesense.data.suveys.surveys.Survey
import kotlinx.coroutines.flow.Flow


class SectionRepository(private val sectionDao: SectionDao) {

    suspend fun add(section: Section){
        sectionDao.addSection(section)
    }

    fun getSectionsByID(id: String) : Flow<List<Section>> {
        return sectionDao.getSectionsByID(id)
    }
}
package com.umaia.movesense.data.suveys

import QuestionTypesRepository
import android.app.Application
import com.umaia.movesense.data.suveys.studies.StudyRepository


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umaia.movesense.data.AppDataBase
import com.umaia.movesense.data.suveys.options.Option
import com.umaia.movesense.data.suveys.options.OptionRepository
import com.umaia.movesense.data.suveys.questions.Question
import com.umaia.movesense.data.suveys.questions.QuestionRepository
import com.umaia.movesense.data.suveys.questions_types.QuestionTypes
import com.umaia.movesense.data.suveys.sections.Section
import com.umaia.movesense.data.suveys.sections.SectionRepository
import com.umaia.movesense.data.suveys.studies.Study
import com.umaia.movesense.data.suveys.surveys.Survey
import com.umaia.movesense.data.suveys.surveys.SurveyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudiesViewmodel(private val application: Application) : ViewModel() {
    private val studyRepository: StudyRepository
    private val surveyRepository: SurveyRepository
    private val sectionRepository : SectionRepository
    private val questionRepository : QuestionRepository
    private val optionRepository : OptionRepository
    private val questionTypesRepository : QuestionTypesRepository

    init {
        val studyDao = AppDataBase.getDatabase(application).studyDao()
        val surveyDao = AppDataBase.getDatabase(application).surveyDao()
        val sectionDao = AppDataBase.getDatabase(application).sectionDao()
        val questionDao = AppDataBase.getDatabase(application).questionDao()
        val optionDao = AppDataBase.getDatabase(application).optionDao()
        val questionTypesDao = AppDataBase.getDatabase(application).questionTypesDao()
        studyRepository =  StudyRepository(studyDao)
        surveyRepository = SurveyRepository(surveyDao)
        sectionRepository = SectionRepository(sectionDao)
        questionRepository = QuestionRepository(questionDao)
        optionRepository = OptionRepository(optionDao)
        questionTypesRepository = QuestionTypesRepository(questionTypesDao)
    }

    fun addTypes(questionTypes:  QuestionTypes){
        viewModelScope.launch(Dispatchers.IO){
            questionTypesRepository.add(questionTypes)
        }
    }

    fun optionAdd(option: Option){
        viewModelScope.launch(Dispatchers.IO){
            optionRepository.add(option)
        }
    }

    fun studyAdd(study: Study){
        viewModelScope.launch(Dispatchers.IO){
            studyRepository.add(study)
        }
    }

    fun surveyAdd(survey: Survey) {
        viewModelScope.launch(Dispatchers.IO) {
            surveyRepository.add(survey)
        }
    }

    fun sectionAdd(section: Section){
        viewModelScope.launch(Dispatchers.IO){
            sectionRepository.add(section)
        }
    }

    fun questionAdd(question: Question){
        viewModelScope.launch(Dispatchers.IO){
            questionRepository.add(question)
        }
    }


}
package com.umaia.movesense.data.suveys

import QuestionTypesRepository
import android.app.Application
import android.graphics.Path.Op
import android.view.KeyEvent.DispatcherState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.umaia.movesense.data.suveys.studies.StudyRepository


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umaia.movesense.data.AppDataBase
import com.umaia.movesense.data.suveys.options.Option
import com.umaia.movesense.data.suveys.options.OptionRepository
import com.umaia.movesense.data.suveys.questions.Question
import com.umaia.movesense.data.suveys.questions.QuestionRepository
import com.umaia.movesense.data.suveys.questions_options.QuestionOption
import com.umaia.movesense.data.suveys.questions_options.QuestionOptionRepository
import com.umaia.movesense.data.suveys.questions_types.QuestionTypes
import com.umaia.movesense.data.suveys.sections.Section
import com.umaia.movesense.data.suveys.sections.SectionRepository
import com.umaia.movesense.data.suveys.studies.Study
import com.umaia.movesense.data.suveys.surveys.Survey
import com.umaia.movesense.data.suveys.surveys.SurveyRepository
import com.umaia.movesense.data.suveys.user_studies.UserStudies
import com.umaia.movesense.data.suveys.user_studies.UserStudiesRepository
import com.umaia.movesense.data.suveys.user_surveys.UserSurveys
import com.umaia.movesense.data.suveys.user_surveys.UserSurveysRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher

class StudiesViewmodel(private val application: Application) : ViewModel() {
    private val studyRepository: StudyRepository
    private val surveyRepository: SurveyRepository
    private val sectionRepository : SectionRepository
    private val questionRepository : QuestionRepository
    private val optionRepository : OptionRepository
    private val questionTypesRepository : QuestionTypesRepository
    private val userStudiesRepository : UserStudiesRepository
    private val userSurveysRepository : UserSurveysRepository
    private val questionOptionRepository: QuestionOptionRepository



    init {
        val studyDao = AppDataBase.getDatabase(application).studyDao()
        val surveyDao = AppDataBase.getDatabase(application).surveyDao()
        val sectionDao = AppDataBase.getDatabase(application).sectionDao()
        val questionDao = AppDataBase.getDatabase(application).questionDao()
        val optionDao = AppDataBase.getDatabase(application).optionDao()
        val questionTypesDao = AppDataBase.getDatabase(application).questionTypesDao()
        val userStudiesDao = AppDataBase.getDatabase(application).userStudiesDao()
        val userSurveysDao  = AppDataBase.getDatabase(application).userSurveysDao()
        val questionOptionDao = AppDataBase.getDatabase(application).questionOptionsDao()
        studyRepository =  StudyRepository(studyDao)
        surveyRepository = SurveyRepository(surveyDao)
        sectionRepository = SectionRepository(sectionDao)
        questionRepository = QuestionRepository(questionDao)
        optionRepository = OptionRepository(optionDao)
        questionTypesRepository = QuestionTypesRepository(questionTypesDao)
        userStudiesRepository = UserStudiesRepository(userStudiesDao)
        userSurveysRepository = UserSurveysRepository(userSurveysDao)
        questionOptionRepository = QuestionOptionRepository(questionOptionDao)


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

    val _options = MutableLiveData<List<Option>>()
    val options : LiveData<List<Option>>
    get() = _options

    fun getOptionByQuestionID(questionID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val options = optionRepository.getOptionByQuestionID(questionID)
            withContext(Dispatchers.Main) {
                _options.value = options
            }
        }
    }

    val _name = MutableLiveData<String>()
    val name : LiveData<String>
        get() = _name

    fun getOptionTextById(optionID: Long): LiveData<String> {
        val optionText = MutableLiveData<String>()
        viewModelScope.launch {
            optionText.postValue(optionRepository.getOptionTextById(optionID))
        }
        return optionText
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

    fun userSurveyAdd(userSurveys: UserSurveys){
        viewModelScope.launch(Dispatchers.IO){
            userSurveysRepository.add(userSurveys)
        }
    }
    fun userStudysAdd(userStudies: UserStudies){
        viewModelScope.launch(Dispatchers.IO){
            userStudiesRepository.add(userStudies)
        }
    }
    fun questionOptionsAdd(questionOption: QuestionOption){
        viewModelScope.launch(Dispatchers.IO){
            questionOptionRepository.add(questionOption)

        }
    }


}
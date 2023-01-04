package com.umaia.movesense.data.suveys.questions_options

import com.umaia.movesense.data.suveys.options.Option
import com.umaia.movesense.data.suveys.options.OptionDao
import kotlinx.coroutines.flow.Flow

class QuestionOptionRepository (private val questionOptionDao: QuestionOptionDao){

    suspend fun add(questionOption: QuestionOption){
        questionOptionDao.addQuestionOption(questionOption)
    }
    fun getOptionsFromQuestions(id: String) : Flow<List<QuestionOption>> {
        return questionOptionDao.getQuestionsBySectionID(id)
    }
}
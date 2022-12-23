package com.umaia.movesense.data.suveys.questions_options

import com.umaia.movesense.data.suveys.options.Option
import com.umaia.movesense.data.suveys.options.OptionDao

class QuestionOptionRepository (private val questionOptionDao: QuestionOptionDao){

    suspend fun add(questionOption: QuestionOption){
        questionOptionDao.addQuestionOption(questionOption)
    }

}
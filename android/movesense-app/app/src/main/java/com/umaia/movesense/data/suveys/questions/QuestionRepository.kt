package com.umaia.movesense.data.suveys.questions

import kotlinx.coroutines.flow.Flow


class QuestionRepository(private val questionDao: QuestionDao) {

    suspend fun add(question: Question){
        questionDao.addQuestion(question)
    }

    fun getQuestionsBySectionID(id: String) : Flow<List<Question>> {
        return questionDao.getQuestionsBySectionID(id)
    }
}
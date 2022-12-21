package com.umaia.movesense.data.suveys.questions



class QuestionRepository(private val questionDao: QuestionDao) {

    suspend fun add(question: Question){
        questionDao.addQuestion(question)
    }
}
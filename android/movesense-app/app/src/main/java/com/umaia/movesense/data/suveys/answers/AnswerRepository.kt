package com.umaia.movesense.data.suveys.answers

import android.graphics.Path.Op
import androidx.lifecycle.LiveData
import com.umaia.movesense.data.suveys.answers.Answer
import com.umaia.movesense.data.suveys.answers.AnswerDao
import kotlinx.coroutines.flow.Flow


class AnswerRepository(private val answerDao: AnswerDao) {

    suspend fun add(answer: Answer) {
        answerDao.addAnswer(answer)
    }

    fun getAllAnswers() : Flow<List<Answer>> {
        return answerDao.getAllAnswers()
    }
}

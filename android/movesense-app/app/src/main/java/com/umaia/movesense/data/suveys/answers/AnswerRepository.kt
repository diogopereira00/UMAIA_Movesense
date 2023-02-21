package com.umaia.movesense.data.suveys.answers

import android.graphics.Path.Op
import androidx.lifecycle.LiveData
import com.umaia.movesense.data.suveys.answers.Answer
import com.umaia.movesense.data.suveys.answers.AnswerDao
import com.umaia.movesense.data.temp.TEMP
import kotlinx.coroutines.flow.Flow


class AnswerRepository(private val answerDao: AnswerDao) {

    suspend fun add(answer: Answer) {
        answerDao.addAnswer(answer)
    }
    val getAllAnswers: LiveData<List<Answer>> = answerDao.getAllAnswers()

    fun deleteByID(id: Long){
        answerDao.deleteById(id)
    }

    fun deleteAll() {
        answerDao.deleteAll()
    }

}

package com.umaia.movesense.data.suveys.options

import android.graphics.Path.Op
import androidx.lifecycle.LiveData


class OptionRepository(private val optionDao: OptionDao) {

    suspend fun add(option: Option) {
        optionDao.addOption(option)
    }

    suspend fun getOptionByQuestionID(questionId: Long) : List<Option>{
         return optionDao.getOptionByQuestionID(questionId)
    }

    suspend fun  getOptionTextById(optionID: Long) : String{
        return optionDao.getOptionTextById(optionID)
    }

    suspend fun getOptionByID(optionID: Long) : Option{
        return optionDao.getOptionByID(optionID)
    }
}

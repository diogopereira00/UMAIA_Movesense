package com.umaia.movesense.data.suveys.options




class OptionRepository(private val optionDao: OptionDao) {

    suspend fun add(option: Option){
        optionDao.addOption(option)
    }
}
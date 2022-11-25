package com.umaia.movesense.data.hr

import androidx.lifecycle.LiveData

class HrRepository(private val hrDao: HrDao) {

    val readAllData: LiveData<List<Hr>> = hrDao.readAllHr()

    suspend fun addHr(hr: Hr){
        hrDao.addHr(hr)
    }
}
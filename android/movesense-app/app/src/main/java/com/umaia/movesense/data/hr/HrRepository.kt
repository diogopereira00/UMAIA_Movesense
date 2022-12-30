package com.umaia.movesense.data.hr

import androidx.lifecycle.LiveData
import com.umaia.movesense.data.ecg.ECG

class HrRepository(private val hrDao: HrDao) {

    val readAllData: LiveData<List<Hr>> = hrDao.readAllHr()

    val getAllHr: LiveData<List<Hr>> = hrDao.getAllHr()
    fun deleteByID(id: Long){
        hrDao.deleteById(id)
    }

    fun deleteAll() {
        hrDao.deleteAll()
    }
    suspend fun add(hr: Hr){
        hrDao.addHr(hr)
    }

    suspend fun getIdFromLastRecord() : Long{
        return hrDao.getIdFromLastRecord()
    }
}
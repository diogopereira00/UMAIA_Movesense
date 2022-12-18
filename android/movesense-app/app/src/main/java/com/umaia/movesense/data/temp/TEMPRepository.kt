package com.umaia.movesense.data.temp

import androidx.lifecycle.LiveData
import com.umaia.movesense.data.magn.TEMPDao

class TEMPRepository(private val tempDao: TEMPDao) {

    val readAllTemp: LiveData<List<TEMP>> = tempDao.readAllTemp()
    val getAllTemp: LiveData<List<TEMP>> = tempDao.getAllTemp()
    suspend fun deleteByID(id: Long){
        tempDao.deleteById(id)
    }

    fun deleteAll() {
        tempDao.deleteAll()
    }
    suspend fun add(temp: TEMP){
        tempDao.addTemp(temp)
    }
}
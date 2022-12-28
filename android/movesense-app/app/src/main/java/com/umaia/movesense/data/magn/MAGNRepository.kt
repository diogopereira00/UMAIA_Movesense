package com.umaia.movesense.data.magn

import androidx.lifecycle.LiveData

class MAGNRepository(private val magnDao: MAGNDao) {

    val readAllACC: LiveData<List<MAGN>> = magnDao.readAllMAGN()
    val getAllMagn: LiveData<List<MAGN>> = magnDao.getAllMagn()
    suspend fun deleteByID(id: Long){
        magnDao.deleteById(id)
    }

    fun deleteAll() {
        magnDao.deleteAll()
    }
    suspend fun add(magn: MAGN){
        magnDao.addMAGNO(magn)
    }

    suspend fun getIdFromLastRecord() : Long{
        return magnDao.getIdFromLastRecord()
    }
}
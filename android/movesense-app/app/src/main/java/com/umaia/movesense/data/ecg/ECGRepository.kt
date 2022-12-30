package com.umaia.movesense.data.ecg

import androidx.lifecycle.LiveData

class ECGRepository(private val ecgDao: ECGDao) {

//    val readAllData: LiveData<List<ECG>> = ecgDao.readAllECG()

    val getAllECG: LiveData<List<ECG>> = ecgDao.getAllECG()

    fun deleteAll() {
        ecgDao.deleteAll()
    }

    suspend fun add(ecg: ECG){
        ecgDao.addHr(ecg)
    }
    fun deleteByID(id: Long){
        ecgDao.deleteById(id)
    }

    suspend fun getIdFromLastRecord() : Long{
        return ecgDao.getIdFromLastRecord()
    }
}
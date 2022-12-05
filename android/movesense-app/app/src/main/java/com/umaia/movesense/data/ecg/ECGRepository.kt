package com.umaia.movesense.data.ecg

import androidx.lifecycle.LiveData

class ECGRepository(private val ecgDao: ECGDao) {

    val readAllData: LiveData<List<ECG>> = ecgDao.readAllECG()

    suspend fun add(ecg: ECG){
        ecgDao.addHr(ecg)
    }
}
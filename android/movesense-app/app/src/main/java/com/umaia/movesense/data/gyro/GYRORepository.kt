package com.umaia.movesense.data.gyro

import androidx.lifecycle.LiveData
import com.umaia.movesense.data.ecg.ECG


class GYRORepository(private val gyroDao: GYRODao) {

    val readAllACC: LiveData<List<GYRO>> = gyroDao.readAllGyro()


    val getAllGYRO: LiveData<List<GYRO>> = gyroDao.getAllGyro()
    suspend fun deleteByID(id: Long){
        gyroDao.deleteById(id)
    }

    fun deleteAll() {
        gyroDao.deleteAll()
    }
    suspend fun add(gyro: GYRO){
        gyroDao.addGYRO(gyro)
    }

    suspend fun getIdFromLastRecord() : Long{
        return gyroDao.getIdFromLastRecord()
    }
}
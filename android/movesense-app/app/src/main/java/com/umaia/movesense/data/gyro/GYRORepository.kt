package com.umaia.movesense.data.gyro

import androidx.lifecycle.LiveData


class GYRORepository(private val gyroDao: GYRODao) {

    val readAllACC: LiveData<List<GYRO>> = gyroDao.readAllGyro()

    suspend fun add(gyro: GYRO){
        gyroDao.addGYRO(gyro)
    }
}
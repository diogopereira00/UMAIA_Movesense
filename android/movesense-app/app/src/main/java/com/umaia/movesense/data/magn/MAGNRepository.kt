package com.umaia.movesense.data.magn

import androidx.lifecycle.LiveData
import com.umaia.movesense.data.acc.ACC
import com.umaia.movesense.data.acc.ACCDao

class MAGNRepository(private val magnDao: MAGNDao) {

    val readAllACC: LiveData<List<MAGN>> = magnDao.readAllMAGN()

    suspend fun add(magn: MAGN){
        magnDao.addMAGNO(magn)
    }
}
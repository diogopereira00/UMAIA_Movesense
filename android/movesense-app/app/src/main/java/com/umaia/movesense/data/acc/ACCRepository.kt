package com.umaia.movesense.data.acc

import androidx.lifecycle.LiveData
import com.umaia.movesense.data.acc.ACC
import com.umaia.movesense.data.acc.ACCDao

class ACCRepository(private val accDao: ACCDao) {

    val readAllACC: LiveData<List<ACC>> = accDao.readAllACC()

    suspend fun add(acc: ACC){
        accDao.addACC(acc)
    }
}
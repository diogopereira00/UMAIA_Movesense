package com.umaia.movesense.data.acc

import androidx.lifecycle.LiveData
import com.umaia.movesense.data.repository.BaseRepository

class ACCRepository(private val accDao: ACCDao) : BaseRepository() {


    val readAllACC: LiveData<List<ACC>> = accDao.readAllACC()

    val getAllACC: LiveData<List<ACC>> = accDao.getAllAcc()
    suspend fun add(acc: ACC) {
        accDao.addACC(acc)
    }

     fun deleteAll(test: Int) {
        accDao.deleteAll()
    }


}
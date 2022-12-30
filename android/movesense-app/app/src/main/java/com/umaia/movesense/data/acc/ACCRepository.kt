package com.umaia.movesense.data.acc

import androidx.lifecycle.LiveData
import com.umaia.movesense.data.suveys.options.repository.BaseRepository

class ACCRepository(private val accDao: ACCDao) : BaseRepository() {


    val readAllACC: LiveData<List<ACC>> = accDao.readAllACC()



    val getAllACC: LiveData<List<ACC>> = accDao.getAllAcc()

    suspend fun add(acc: ACC) {
        accDao.addACC(acc)
    }
    fun deleteByID(id: Long){
       accDao.deleteById(id)
    }

     suspend  fun deleteAll() {
        accDao.deleteAll()
    }

    suspend fun getIdFromLastRecord() : Long{
        return accDao.getIdFromLastRecord()
    }


}
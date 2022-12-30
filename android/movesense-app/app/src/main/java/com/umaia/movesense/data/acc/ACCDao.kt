package com.umaia.movesense.data.acc

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ACCDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addACC(acc: ACC)

    @Query("SELECT * FROM acc_table ORDER BY id ASC")
    fun readAllACC(): LiveData<List<ACC>>

    @Query("SELECT * FROM ACC_TABLE WHERE id !=(SELECT MAX(id) FROM ACC_TABLE) ORDER BY ID")
    fun getAllAcc(): LiveData<List<ACC>>

    //@Query("DELETE FROM acc_table WHERE id in (SELECT id from acc_table limit :id)")
    @Query("DELETE FROM acc_table WHERE id <(SELECT MAX(id) FROM ACC_TABLE)")
    suspend fun deleteAll()

    @Query("DELETE FROM acc_table WHERE id = :id")
    fun deleteById(id: Long) : Int

    @Query("SELECT id from acc_table ORDER BY id DESC LIMIT 1")
    suspend fun getIdFromLastRecord() : Long

}
package com.umaia.movesense.data.magn

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.umaia.movesense.data.acc.ACC

@Dao
interface MAGNDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMAGNO(magn: MAGN)

    @Query("SELECT * FROM magn_table ORDER BY id ASC")
    fun readAllMAGN(): LiveData<List<MAGN>>

    @Query("SELECT * FROM magn_table WHERE id !=(SELECT MAX(id) FROM magn_table) ORDER BY ID")
    fun getAllMagn(): LiveData<List<MAGN>>

    //@Query("DELETE FROM acc_table WHERE id in (SELECT id from acc_table limit :id)")
    @Query("DELETE FROM magn_table WHERE id <(SELECT MAX(id) FROM magn_table)")
    fun deleteAll()

    @Query("DELETE FROM magn_table WHERE id = :id")
    fun deleteById(id: Long)

    @Query("SELECT id from magn_table ORDER BY id DESC LIMIT 1")
    suspend fun getIdFromLastRecord() : Long

    @Query("SELECT * FROM MAGN_TABLE WHERE id !=(SELECT MAX(id) FROM MAGN_TABLE) ORDER BY ID")
    suspend fun getAll(): List<MAGN>
}
package com.umaia.movesense.data.magn

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.umaia.movesense.data.temp.TEMP

@Dao
interface TEMPDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTemp(temp: TEMP)

    @Query("SELECT * FROM temp_table ORDER BY id ASC")
    fun readAllTemp(): LiveData<List<TEMP>>

    @Query("SELECT * FROM temp_table WHERE id !=(SELECT MAX(id) FROM TEMP_TABLE) ORDER BY ID")
    fun getAllTemp(): LiveData<List<TEMP>>

    //@Query("DELETE FROM acc_table WHERE id in (SELECT id from acc_table limit :id)")
    @Query("DELETE FROM temp_table WHERE id <(SELECT MAX(id) FROM TEMP_TABLE)")
    fun deleteAll()

    @Query("DELETE FROM temp_table WHERE id = :id")
    fun deleteById(id: Long)

    @Query("SELECT id from temp_table ORDER BY id DESC LIMIT 1")
    suspend fun getIdFromLastRecord() : Long

    @Query("SELECT * FROM temp_table WHERE id !=(SELECT MAX(id) FROM temp_table) ORDER BY ID")
    suspend fun getAll(): List<TEMP>
}
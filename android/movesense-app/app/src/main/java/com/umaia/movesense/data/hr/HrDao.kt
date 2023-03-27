package com.umaia.movesense.data.hr

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.umaia.movesense.data.acc.ACC

@Dao
interface HrDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addHr(hr: Hr)

    @Query("SELECT * FROM hr_table ORDER BY id ASC")
    fun readAllHr(): LiveData<List<Hr>>

    @Query("SELECT * FROM hr_table WHERE id !=(SELECT MAX(id) FROM ACC_TABLE) ORDER BY ID")
    fun getAllHr(): LiveData<List<Hr>>

    //@Query("DELETE FROM acc_table WHERE id in (SELECT id from acc_table limit :id)")
    @Query("DELETE FROM hr_table WHERE id <(SELECT MAX(id) FROM ACC_TABLE)")
    fun deleteAll()

    @Query("DELETE FROM hr_table WHERE id = :id")
    fun deleteById(id: Long)

    @Query("SELECT id from hr_table ORDER BY id DESC LIMIT 1")
    suspend fun getIdFromLastRecord() : Long

    @Query("SELECT * FROM HR_TABLE WHERE id !=(SELECT MAX(id) FROM HR_TABLE) ORDER BY ID")
    suspend fun getAll(): List<Hr>
}
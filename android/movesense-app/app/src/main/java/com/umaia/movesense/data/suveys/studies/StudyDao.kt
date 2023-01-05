package com.umaia.movesense.data.suveys.studies

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.umaia.movesense.data.acc.ACC
import com.umaia.movesense.data.hr.Hr
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStudy(study: Study)

    @Query("SELECT version FROM studies_table WHERE id = :id")
    suspend fun getVersionByID(id : String): Double

    @Query("SELECT adminPassword from studies_table where id = :id")
    suspend fun getAdminPassword(id: String) : String


}
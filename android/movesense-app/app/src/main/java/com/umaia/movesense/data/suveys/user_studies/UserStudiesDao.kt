package com.umaia.movesense.data.suveys.user_studies

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.umaia.movesense.data.suveys.surveys.Survey

@Dao
interface UserStudiesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUserStudy(userStudies: UserStudies)

}
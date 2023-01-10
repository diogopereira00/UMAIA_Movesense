package com.umaia.movesense.data.suveys.user_surveys

import androidx.room.*
import com.umaia.movesense.data.suveys.user_studies.UserStudies
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSurveysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUserSurvey(userSurveys: UserSurveys): Long

    @Query("SELECT id from user_surveys ORDER BY id DESC LIMIT 1")
    suspend fun getIdFromLastRecord(): Long
}
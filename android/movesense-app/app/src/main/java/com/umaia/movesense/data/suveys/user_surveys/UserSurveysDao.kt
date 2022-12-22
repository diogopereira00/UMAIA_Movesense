package com.umaia.movesense.data.suveys.user_surveys

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.umaia.movesense.data.suveys.user_studies.UserStudies

@Dao
interface UserSurveysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUserSurvey(userSurveys: UserSurveys)

}